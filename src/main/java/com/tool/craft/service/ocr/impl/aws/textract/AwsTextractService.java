package com.tool.craft.service.ocr.impl.aws.textract;

import com.tool.craft.service.ocr.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

@Log4j2
@Service
public class AwsTextractService implements TextAndKeyValuePairsService {

    @Override
    public TextsAndKeyValuePairs findTextsAndKeyValuePairsIn(InputStream inputStream) {
        try {
            log.info("Iniciando processo de OCR");
            TextractClient textractClient = TextractClient.builder().region(Region.US_EAST_1).build();
            SdkBytes sourceBytes = SdkBytes.fromInputStream(inputStream);

            Document myDoc = Document.builder()
                    .bytes(sourceBytes)
                    .build();

            AnalyzeDocumentRequest analyzeDocumentRequest = AnalyzeDocumentRequest.builder()
                    .document(myDoc)
                    .featureTypes(FeatureType.FORMS)
                    .build();

            log.info("Abrindo conexão com a AWS");
            AnalyzeDocumentResponse analyzeDocumentResponse = textractClient.analyzeDocument(analyzeDocumentRequest);
            log.info("Recebendo transcrição da AWS");

            List<Block> blocks = analyzeDocumentResponse.blocks();

            Map<String, Block> blockMap = blocks.stream().collect(toMap(Block::id, Function.identity()));

            Map<String, Block> keyMap = blocks.stream()
                    .filter(d -> d.blockType().equals(BlockType.KEY_VALUE_SET) && d.entityTypes().contains(EntityType.KEY))
                    .collect(toMap(Block::id, Function.identity()));

            Map<String, Block> valueMap = blocks.stream()
                    .filter(d -> d.blockType().equals(BlockType.KEY_VALUE_SET) && d.entityTypes().contains(EntityType.VALUE))
                    .collect(toMap(Block::id, Function.identity()));

            List<KeyValuePairs> keyValuePairs = createKeyValuePairs(keyMap, valueMap, blockMap)
                    .entrySet()
                    .stream().map(e -> new TextractKeyValuePairs(e.getKey(), e.getValue()))
                    .collect(toList());

            List<Text> onlyLines = analyzeDocumentResponse
                    .blocks()
                    .stream()
                    .filter(b -> b.blockType().equals(BlockType.LINE))
                    .map(TextractText::new)
                    .collect(toList());

            return new TextractTextsAndKeyValuePairs(onlyLines,keyValuePairs);

        } catch (TextractException e) {
            System.err.println(e.getMessage());
        }
        return new TextractTextsAndKeyValuePairs();
    }


    public Optional<Block>findValueBlock(Block keyBlock, Map<String, Block> valueMap) {
        for (Relationship relationship : keyBlock.relationships()) {
            if (relationship.type().equals(RelationshipType.VALUE)) {
                for (String id : relationship.ids()) {
                    return Optional.ofNullable(valueMap.get(id));
                }
            }
        }
        return Optional.empty();
    }

    public Map<String, String> createKeyValuePairs(Map<String, Block> keyMap, Map<String, Block> valueMap, Map<String, Block> blockMap) {

        Map<String, String> keyValueRelationShip = new HashMap<>();

        for (Map.Entry<String, Block> entry : keyMap.entrySet()) {
            findValueBlock(entry.getValue(), valueMap)
                .ifPresent(valueBlock ->{
                    String key = getText(entry.getValue(), blockMap);
                    String value = getText(valueBlock, blockMap);
                    keyValueRelationShip.put(key, value);
                });
        }

        return keyValueRelationShip;
    }

    public String getText(Block block, Map<String, Block> blockMap) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Relationship relationship : block.relationships()) {
            if (relationship.type().equals(RelationshipType.CHILD)) {
                for (String id : relationship.ids()) {
                    Block word = blockMap.get(id);

                    if (word.blockType().equals(BlockType.WORD))
                        stringBuilder.append(word.text()).append(" ");

                    if (word.blockType().equals(BlockType.SELECTION_ELEMENT))
                        if (word.selectionStatus().equals(SelectionStatus.SELECTED))
                            stringBuilder.append("X").append(" ");
                }
            }
        }
        return stringBuilder.toString();
    }

}
