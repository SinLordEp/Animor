package com.example.animor.Model;

import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.dto.TagDTO;

import java.util.List;

public class StartupResource {
    private List<SpeciesDTO> speciesDTOS;
    private List<TagDTO> tags;
    private String deviceToken;

    public StartupResource(List<SpeciesDTO> speciesDTOS, List<TagDTO> tags, String deviceToken) {
        this.speciesDTOS = speciesDTOS;
        this.tags = tags;
        this.deviceToken = deviceToken;
    }

    public List<SpeciesDTO> getSpecies() {
        return speciesDTOS;
    }

    public void setSpecies(List<SpeciesDTO> speciesDTOS) {
        this.speciesDTOS = speciesDTOS;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
