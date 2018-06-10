/*
 * Copyright(c) 2016-2017 IBM, Red Hat, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.microprofile.showcase.speaker.domain;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;

import io.microprofile.showcase.speaker.model.Speaker;

public class VenueJavaOne2016 extends Venue {

    private final String name = "JavaOne2016";
    private final Logger log = Logger.getLogger(VenueJavaOne2016.class.getName());

    VenueJavaOne2016() throws MalformedURLException { }

    @Override
    public String getName() {
        return this.name;
    }

    public Set<Speaker> getSpeakers() {

        final Set<Speaker> speakers = new HashSet<>();

        if (speakers.isEmpty()) {
            try {
                speakers.addAll(this.getSpeakersFile());
            } catch (final IOException e) {
                this.log.log(Level.SEVERE, "Failed to read fallback json", e);
            }
        }

        return speakers;
    }

    private Set<Speaker> getSpeakersFile() throws IOException {
        Set<Speaker> speakers = new HashSet<>();
        final InputStream is = this.getClass().getResourceAsStream("/ConferenceData.json");
        JsonReaderFactory factory = Json.createReaderFactory(null);
        JsonReader reader = factory.createReader(is);
        JsonArray speakerList = reader.readArray();

        for (JsonValue item : speakerList) {
            JsonObject speaker = (JsonObject) item;
            Speaker speakerObj = new Speaker(speaker);
            speakers.add(speakerObj);
        }
        return speakers;
    }

}
