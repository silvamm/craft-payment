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
public class AwsTextractService implements AnalyzeDocumentService {

    @Override
    public AnalysedDocument analyseDocumentoFrom(InputStream inputStream) {
        try {
            log.info("Iniciando processo de OCR - Via Aws Textract");
            TextractClient textractClient = TextractClient.builder().region(Region.US_EAST_1).build();
            SdkBytes sourceBytes = SdkBytes.fromInputStream(inputStream);

            Document myDoc = Document.builder()
                    .bytes(sourceBytes)
                    .build();

            AnalyzeDocumentRequest analyzeDocumentRequest = AnalyzeDocumentRequest.builder()
                    .document(myDoc)
                    .featureTypes(FeatureType.FORMS)
                    .build();

            log.info("Enviando arquivo para Aws");
            AnalyzeDocumentResponse analyzeDocumentResponse = textractClient.analyzeDocument(analyzeDocumentRequest);
            log.info("Recebendo transcrição da Aws");

            List<Block> blocks = analyzeDocumentResponse.blocks();

            List<Text> lines = onlyLines(analyzeDocumentResponse);

            List<LabelAndInputValue> keyValuePairs = createKeyValuePairs(blocks, lines)
                    .stream()
                    .map(e -> new TextractLabelAndInputValue(e.getLabel(), e.getInputValue()))
                    .collect(toList());

            return new TextractTextsAndKeyValuePairs(lines, keyValuePairs);

        } catch (TextractException e) {
            log.error(e.getMessage());
        }

        return new TextractTextsAndKeyValuePairs();
    }

    private List<Text> onlyLines(AnalyzeDocumentResponse analyzeDocumentResponse) {
        return analyzeDocumentResponse
                .blocks()
                .stream()
                .filter(b -> b.blockType().equals(BlockType.LINE))
                .map(TextractText::new)
                .collect(toList());
    }

    public List<LabelAndInputValue> createKeyValuePairs(List<Block> blocks,
                                                        List<Text> lines) {

        Map<String, Block> blockMap = blocks.stream().collect(toMap(Block::id, Function.identity()));

        Map<String, Block> keyMap = blocks.stream()
                .filter(this::isKey)
                .collect(toMap(Block::id, Function.identity()));

        Map<String, Block> valueMap = blocks.stream()
                .filter(this::isValue)
                .collect(toMap(Block::id, Function.identity()));

        List<LabelAndInputValue> keyValuePairs = new ArrayList<>();

        for (Map.Entry<String, Block> entry : keyMap.entrySet()) {
            findValueBlock(entry.getValue(), valueMap)
                .ifPresent(valueBlock ->{
                    String key = buildText(entry.getValue(), blockMap);
                    String value = buildText(valueBlock, blockMap);
                    Text keyText = findTextIn(lines, key).orElseGet(() -> new TextractText(key));
                    Text valueText = findTextIn(lines, value).orElseGet(() -> new TextractText(value));
                    keyValuePairs.add(new TextractLabelAndInputValue(keyText, valueText));
                });
        }

        return keyValuePairs;
    }

    private boolean isValue(Block block) {
        return block.blockType().equals(BlockType.KEY_VALUE_SET) && block.entityTypes().contains(EntityType.VALUE);
    }

    private boolean isKey(Block block) {
        return block.blockType().equals(BlockType.KEY_VALUE_SET) && block.entityTypes().contains(EntityType.KEY);
    }

    public String buildText(Block block, Map<String, Block> blockMap) {
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

    public Optional<Block> findValueBlock(Block keyBlock, Map<String, Block> valueMap) {
        for (Relationship relationship : keyBlock.relationships()) {
            if (relationship.type().equals(RelationshipType.VALUE)) {
                for (String id : relationship.ids()) {
                    return Optional.ofNullable(valueMap.get(id));
                }
            }
        }
        return Optional.empty();
    }

    private Optional<Text> findTextIn(List<Text> onlyLines, String text){
        return onlyLines.stream()
                .filter(line -> line.contains(text))
                .findFirst();
    }

}
