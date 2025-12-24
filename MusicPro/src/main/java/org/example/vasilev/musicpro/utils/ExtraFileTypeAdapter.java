package org.example.vasilev.musicpro.utils;

import com.google.gson.*;
import org.example.vasilev.musicpro.models.ExtraFileType;

import java.lang.reflect.Type;

public class ExtraFileTypeAdapter implements JsonSerializer<ExtraFileType>, JsonDeserializer<ExtraFileType>
{
    @Override
    public ExtraFileType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        try
        {
            if (json.isJsonPrimitive())
            {
                JsonPrimitive primitive = json.getAsJsonPrimitive();
                if (primitive.isNumber())
                {
                    // Десериализация из числа (int)
                    int intValue = primitive.getAsInt();
                    return ExtraFileType.fromValue(intValue);
                }
            }
        }
        catch (Exception e)
        {
            throw new JsonParseException("Failed to parse ExtraFileType from: " + json, e);
        }

        return ExtraFileType.OTHER;
    }

    @Override
    public JsonElement serialize(ExtraFileType src, Type typeOfSrc, JsonSerializationContext context)
    {
        // Сериализуем в число (int)
        return new JsonPrimitive(src.getValue());
    }
}
