package com.example.animor.Model;

import java.util.List;

public class StartupResource {
    private List<Species> species;
    private List<Tag> tags;
    private String deviceToken;

    public StartupResource(List<Species> species, List<Tag> tags, String deviceToken) {
        this.species = species;
        this.tags = tags;
        this.deviceToken = deviceToken;
    }

    public List<Species> getSpecies() {
        return species;
    }

    public void setSpecies(List<Species> species) {
        this.species = species;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
