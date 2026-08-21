package com.maozi.ai.ai.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pengjinlong
 * @since 2026/8/17 03:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String messageType;

    private Map<String,Object> metadata = Map.of();

    @JsonSerialize(contentUsing = MediaSerializer.class)
    @JsonDeserialize(contentUsing = MediaDeserializer.class)
    private List<Media> media = List.of();

    private List<AssistantMessage.ToolCall> toolCalls = List.of();

    private String textContent;

    private List<ToolResponseMessage.ToolResponse> toolResponses = List.of();

    private Map<String, Object> params = Map.of();

    /**
     * Media 反序列化器
     * <p>
     * {@link Media} 为不可变对象，无默认构造器，Jackson 无法自动反序列化，
     * 需解析 JSON 后通过 Builder 手动构建。data 字段兼容两种形态：
     * byte[]（序列化为整型数组）与 URI（序列化为字符串）。
     * </p>
     */
    public static class MediaDeserializer extends JsonDeserializer<Media> {

        @Override
        public Media deserialize(JsonParser parser, DeserializationContext context) throws IOException {

            JsonNode node = parser.getCodec().readTree(parser);

            JsonNode mimeTypeNode = node.get("mimeType");

            Map<String, String> parameters = new LinkedHashMap<>();
            JsonNode parametersNode = mimeTypeNode.get("parameters");
            parametersNode.fieldNames().forEachRemaining(name -> parameters.put(name, parametersNode.get(name).asText()));

            Media.Builder builder = Media.builder()
                    .mimeType(new MimeType(mimeTypeNode.get("type").asText(), mimeTypeNode.get("subtype").asText(), parameters));

            JsonNode dataNode = node.get("data");
            if (dataNode.isArray()) {
                byte[] data = new byte[dataNode.size()];
                for (int i = 0; i < data.length; i++) {
                    data[i] = (byte) dataNode.get(i).asInt();
                }
                builder.data(data);
            } else {
                builder.data(dataNode.asText());
            }

            if (node.hasNonNull("id")) {
                builder.id(node.get("id").asText());
            }

            if (node.hasNonNull("name")) {
                builder.name(node.get("name").asText());
            }

            return builder.build();

        }

    }

    /**
     * Media 序列化器
     * <p>
     * 与 {@link MediaDeserializer} 对称，序列化为约定的 JSON 结构：
     * data 为 byte[] 时输出整型数组（与 hutool 序列化格式一致，而非 Jackson 默认的 base64 字符串），
     * 为 URI 时输出字符串，保证序列化与反序列化可无损往返。
     * </p>
     */
    public static class MediaSerializer extends JsonSerializer<Media> {

        @Override
        public void serialize(Media media, JsonGenerator generator, SerializerProvider provider) throws IOException {

            generator.writeStartObject();

            if (media.getId() != null) {
                generator.writeStringField("id", media.getId());
            }

            MimeType mimeType = media.getMimeType();
            generator.writeObjectFieldStart("mimeType");
            generator.writeStringField("type", mimeType.getType());
            generator.writeStringField("subtype", mimeType.getSubtype());
            generator.writeObjectField("parameters", mimeType.getParameters());
            generator.writeEndObject();

            Object data = media.getData();
            if (data instanceof byte[] bytes) {
                generator.writeArrayFieldStart("data");
                for (byte item : bytes) {
                    generator.writeNumber(item);
                }
                generator.writeEndArray();
            } else {
                generator.writeStringField("data", String.valueOf(data));
            }

            generator.writeStringField("name", media.getName());

            generator.writeEndObject();

        }

    }

}
