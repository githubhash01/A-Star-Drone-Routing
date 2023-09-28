//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package uk.ac.ed.inf;

import com.google.gson.*;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.lang.reflect.Type;

public class NamedRegionDeserializer implements JsonDeserializer<NamedRegion> {
    public NamedRegionDeserializer() {
    }

    public NamedRegion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        Gson gson = new Gson();
        return gson.fromJson(json, NamedRegion.class);
    }
}
