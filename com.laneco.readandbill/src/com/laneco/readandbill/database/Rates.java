package com.laneco.readandbill.database;

public class Rates extends com.generic.readandbill.database.Rates {
    private double feedTariffAllowance;
    protected double icera;
    protected double overUnderRecovery;
    protected double realPropertyTax;


    protected double businessTax;

    protected double transmissionSystemCharge;
    protected double systemLossTransmission;
    protected double ucStrandedContractCost;
    private double ucmeRed;
    protected double vatDcDemand;
    protected double vatDcDistribution;
    protected double vatGensys;
    protected double vatHostComm;
    protected double vatIcera;
    protected double vatLifelineSubsidy;
    protected double vatMcRetail;
    protected double vatMcSystem;
    protected double vatOverUnderRecovery;
    protected double vatPARR;
    protected double vatPrevYearAdjPowerCost;
    protected double vatReinvestmentFundSustCapex;
    protected double vatScRetail;
    protected double vatScSupply;
    protected double vatSeniorCitizen;
    protected double vatSystemLoss;
    protected double vatSystemLossTransmission;
    protected double vatTcDemand;
    protected double vatTcSystem;

    protected String isLifeLine;
    protected double geaAll;
    protected double rec;
    protected double regulatedNGCPCharge;
    protected double ancillaryServiceCharge;

    public double getRegulatedNGCPCharge() {
        return regulatedNGCPCharge;
    }

    public void setRegulatedNGCPCharge(double regulatedNGCPCharge) {
        this.regulatedNGCPCharge = regulatedNGCPCharge;
    }

    public double getAncillaryServiceCharge() {
        return ancillaryServiceCharge;
    }

    public void setAncillaryServiceCharge(double ancillaryServiceCharge) {
        this.ancillaryServiceCharge = ancillaryServiceCharge;
    }

    public Rates() {
        this.systemLoss = 0.0d;
        this.feedTariffAllowance = 0.0d;
        this.ucStrandedContractCost = 0.0d;
        this.icera = 0.0d;
        this.overUnderRecovery = 0.0d;
        this.vatGensys = 0.0d;
        this.vatHostComm = 0.0d;
        this.vatSystemLoss = 0.0d;
        this.vatIcera = 0.0d;
        this.vatPARR = 0.0d;
        this.businessTax = 0.0d;
        this.transmissionSystemCharge = 0.0d;
        this.realPropertyTax = 0.0d;
        this.vatTcSystem = 0.0d;
        this.vatSystemLossTransmission = 0.0d;
        this.vatDcDemand = 0.0d;
        this.vatDcDistribution = 0.0d;
        this.vatScRetail = 0.0d;
        this.vatScSupply = 0.0d;
        this.vatMcRetail = 0.0d;
        this.vatMcSystem = 0.0d;
        this.vatLifelineSubsidy = 0.0d;
        this.vatSeniorCitizen = 0.0d;
        this.vatReinvestmentFundSustCapex = 0.0d;
        this.vatPrevYearAdjPowerCost = 0.0d;
        this.vatOverUnderRecovery = 0.0d;
        this.isLifeLine = "";
        this.geaAll = 0.0d;
        this.rec = 0.0d;
        this. regulatedNGCPCharge= 0.0d;
        this. ancillaryServiceCharge= 0.0d;
    }

    public double getSystemLossTransmission() {
        return systemLossTransmission;
    }

    public void setSystemLossTransmission(double systemLossTransmission) {
        this.systemLossTransmission = systemLossTransmission;
    }

    public double getUcStrandedContractCost() {
        return ucStrandedContractCost;
    }

    public void setUcStrandedContractCost(double ucStrandedContractCost) {
        this.ucStrandedContractCost = ucStrandedContractCost;
    }

    public double getIcera() {
        return icera;
    }

    public void setIcera(double icera) {
        this.icera = icera;
    }

    public double getOverUnderRecovery() {
        return overUnderRecovery;
    }

    public void setOverUnderRecovery(double overUnderRecovery) {
        this.overUnderRecovery = overUnderRecovery;
    }

    public double getVatGensys() {
        return vatGensys;
    }

    public void setVatGensys(double vatGensys) {
        this.vatGensys = vatGensys;
    }

    public double getVatHostComm() {
        return vatHostComm;
    }

    public void setVatHostComm(double vatHostComm) {
        this.vatHostComm = vatHostComm;
    }

    public double getVatSystemLoss() {
        return vatSystemLoss;
    }

    public void setVatSystemLoss(double vatSystemLoss) {
        this.vatSystemLoss = vatSystemLoss;
    }

    public double getVatPARR() {
        return vatPARR;
    }

    public void setVatPARR(double vatPARR) {
        this.vatPARR = vatPARR;
    }

    public double getVatTcSystem() {
        return vatTcSystem;
    }

    public void setVatTcSystem(double vatTcSystem) {
        this.vatTcSystem = vatTcSystem;
    }

    public double getVatTcDemand() {
        return vatTcDemand;
    }

    public void setVatTcDemand(double vatTcDemand) {
        this.vatTcDemand = vatTcDemand;
    }

    public double getVatSystemLossTransmission() {
        return vatSystemLossTransmission;
    }

    public double getVatIcera() {
        return vatIcera;
    }

    public void setVatIcera(double vatIcera) {
        this.vatIcera = vatIcera;
    }

    public void setVatSystemLossTransmission(double vatSystemLossTransmission) {
        this.vatSystemLossTransmission = vatSystemLossTransmission;
    }

    public double getVatDcDemand() {
        return vatDcDemand;
    }

    public void setVatDcDemand(double vatDcDemand) {
        this.vatDcDemand = vatDcDemand;
    }

    public double getVatDcDistribution() {
        return vatDcDistribution;
    }

    public void setVatDcDistribution(double vatDcDistribution) {
        this.vatDcDistribution = vatDcDistribution;
    }

    public double getRealPropertyTax() {
        return realPropertyTax;
    }

    public void setRealPropertyTax(double realPropertyTax) {
        this.realPropertyTax = realPropertyTax;
    }

    public double getBusinessTax() {
        return businessTax;
    }

    public void setBusinessTax(double businessTax) {
        this.businessTax = businessTax;
    }

    public double getTransmissionSystemCharge() {
        return transmissionSystemCharge;
    }

    public void setTransmissionSystemCharge(double transmissionSystemCharge) {
        this.transmissionSystemCharge = transmissionSystemCharge;
    }

    public double getVatScRetail() {
        return vatScRetail;
    }

    public void setVatScRetail(double vatScRetail) {
        this.vatScRetail = vatScRetail;
    }

    public double getVatScSupply() {
        return vatScSupply;
    }

    public void setVatScSupply(double vatScSupply) {
        this.vatScSupply = vatScSupply;
    }

    public double getVatMcRetail() {
        return vatMcRetail;
    }

    public void setVatMcRetail(double vatMcRetail) {
        this.vatMcRetail = vatMcRetail;
    }

    public double getVatMcSystem() {
        return vatMcSystem;
    }

    public void setVatMcSystem(double vatMcSystem) {
        this.vatMcSystem = vatMcSystem;
    }

    public double getVatLifelineSubsidy() {
        return vatLifelineSubsidy;
    }

    public void setVatLifelineSubsidy(double vatLifelineSubsidy) {
        this.vatLifelineSubsidy = vatLifelineSubsidy;
    }

    public double getVatSeniorCitizen() {
        return vatSeniorCitizen;
    }

    public void setVatSeniorCitizen(double vatSeniorCitizen) {
        this.vatSeniorCitizen = vatSeniorCitizen;
    }

    public double getVatReinvestmentFundSustCapex() {
        return vatReinvestmentFundSustCapex;
    }

    public void setVatReinvestmentFundSustCapex(double vatReinvestmentFundSustCapex) {
        this.vatReinvestmentFundSustCapex = vatReinvestmentFundSustCapex;
    }

    public double getVatPrevYearAdjPowerCost() {
        return vatPrevYearAdjPowerCost;
    }

    public void setVatPrevYearAdjPowerCost(double vatPrevYearAdjPowerCost) {
        this.vatPrevYearAdjPowerCost = vatPrevYearAdjPowerCost;
    }

    public double getVatOverUnderRecovery() {
        return vatOverUnderRecovery;
    }

    public void setVatOverUnderRecovery(double vatOverUnderRecovery) {
        this.vatOverUnderRecovery = vatOverUnderRecovery;
    }

    public double getUcmeRed() {
        return ucmeRed;
    }

    public void setUcmeRed(double ucmeRed) {
        this.ucmeRed = ucmeRed;
    }

    public double getFeedTariffAllowance() {
        return feedTariffAllowance;
    }

    public void setFeedTariffAllowance(double feedTariffAllowance) {
        this.feedTariffAllowance = feedTariffAllowance;
    }

    public String getIsLifeLine() {
        return isLifeLine;
    }

    public double getGeaAll() {
        return geaAll;
    }

    public void setGeaAll(double geaAll) {
        this.geaAll = geaAll;
    }

    public double getRec() {
        return rec;
    }

    public void setRec(double rec) {
        this.rec = rec;
    }


    public void setIsLifeLine(String isLifeLine) {
        this.isLifeLine = isLifeLine;
    }
}