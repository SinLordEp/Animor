package com.example.animor.Model;

import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.dto.TagDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StartupResource {
    @JsonProperty("deviceToken")
    private String deviceToken;
    @JsonProperty("speciesDTOList")
    private List<SpeciesDTO> speciesDTOList;
    @JsonProperty("tagDTOList")
    private List<TagDTO> tagDTOList;

    public StartupResource() {
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public List<SpeciesDTO> getSpeciesDTOList() {
        return speciesDTOList;
    }

    public void setSpeciesDTOList(List<SpeciesDTO> speciesDTOList) {
        this.speciesDTOList = speciesDTOList;
    }

    public List<TagDTO> getTagDTOList() {
        return tagDTOList;
    }

    public void setTagDTOList(List<TagDTO> tagDTOList) {
        this.tagDTOList = tagDTOList;
    }
}
