package eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto;

import java.time.Instant;
import java.util.List;

public class MTQuery {
    private List<String> assetIds;
    private List<String> mobileterminalTypes;
    private List<String> serialNumbers;
    private List<Integer> memberNumbers;
    private List<Integer> dnids;
    private List<String> sateliteNumbers;
    private List<String> softwareVersions;
    private List<String> tranceiverTypes;
    private List<String> antennas;
    private List<String> mobileterminalIds;
    private List<String> historyIds;
    private Instant date;


    public List<String> getAssetIds() {
        return assetIds;
    }

    public void setAssetIds(List<String> assetIds) {
        this.assetIds = assetIds;
    }

    public List<String> getMobileterminalTypes() {
        return mobileterminalTypes;
    }

    public void setMobileterminalTypes(List<String> mobileterminalTypes) {
        this.mobileterminalTypes = mobileterminalTypes;
    }

    public List<String> getSerialNumbers() {
        return serialNumbers;
    }

    public void setSerialNumbers(List<String> serialNumbers) {
        this.serialNumbers = serialNumbers;
    }

    public List<Integer> getMemberNumbers() {
        return memberNumbers;
    }

    public void setMemberNumbers(List<Integer> memberNumbers) {
        this.memberNumbers = memberNumbers;
    }

    public List<Integer> getDnids() {
        return dnids;
    }

    public void setDnids(List<Integer> dnids) {
        this.dnids = dnids;
    }

    public List<String> getSateliteNumbers() {
        return sateliteNumbers;
    }

    public void setSateliteNumbers(List<String> sateliteNumbers) {
        this.sateliteNumbers = sateliteNumbers;
    }

    public List<String> getSoftwareVersions() {
        return softwareVersions;
    }

    public void setSoftwareVersions(List<String> softwareVersions) {
        this.softwareVersions = softwareVersions;
    }

    public List<String> getTranceiverTypes() {
        return tranceiverTypes;
    }

    public void setTranceiverTypes(List<String> tranceiverTypes) {
        this.tranceiverTypes = tranceiverTypes;
    }

    public List<String> getAntennas() {
        return antennas;
    }

    public void setAntennas(List<String> antennas) {
        this.antennas = antennas;
    }

    public List<String> getMobileterminalIds() {
        return mobileterminalIds;
    }

    public void setMobileterminalIds(List<String> mobileterminalIds) {
        this.mobileterminalIds = mobileterminalIds;
    }

    public List<String> getHistoryIds() {
        return historyIds;
    }

    public void setHistoryIds(List<String> historyIds) {
        this.historyIds = historyIds;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }
}
