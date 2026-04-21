package com.laneco.readandbill.database;

import android.content.Context;
import android.util.Log;

import com.androidapp.mytools.objectmanager.DoubleManager;

public class ComputeCharges extends com.generic.readandbill.database.ComputeCharges {
    private RateDataSource dsRates;
    private Consumer lanecoConsumer;
    private Rates rate;

    public ComputeCharges(Context context, Consumer consumer) {
        super(context, consumer);
        this.dsRates = new RateDataSource(context);
        setLanecoConsumer(consumer);
    }

    public void setLanecoConsumer(Consumer lanecoConsumer) {
        this.lanecoConsumer = lanecoConsumer;
        setConsumer(lanecoConsumer);
        this.rate = dsRates.getConsumerRate(lanecoConsumer.getRateCode());
    }

    public double getKilowatthour() {
        return super.getKilowatthour() + lanecoConsumer.getTransLoss();
    }

//	    public double getLifelineKilowatthour() {
//	        double result = getKilowatthour();
//	        if (!lanecoConsumer.getRateCode().equals("R") || result > 20.0d) {
//	            return result;
//	        }
//	        return result - (getLifelineDiscount(result) * result);
//	    }

    public double getLifelineKilowatthour() {
        double result = getKilowatthour();
//		if (!lanecoConsumer.getRateCode().equals("R") || result > 20.0d) {
//			return result;
//		}
        if (!lanecoConsumer.getRateCode().equals("R") || result > -20.0d) {
            return result;
        }
        return result - ((result) * result);
    }

    public double getLifelineKilowatthourDisplay() {
        double result = getKilowatthour();
        if (!lanecoConsumer.getRateCode().equals("R") || result > 20.0d) {
            return 0.0d;
        }
        return result - (getLifelineDiscount(result) * result);
    }

    public double getSeniorCitizenDiscountSubsidy() {
        if (!lanecoConsumer.getRateCode().equals("R") || getKilowatthour() > 100.0d) {
            if ((!lanecoConsumer.getRateCode().equals("R") || getKilowatthour() <= 100.0d) && lanecoConsumer.getRateCode().equals("R")) {
                return 0.0d;
            }
            return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getSeniorCitizenSubsidy())).doubleValue();
        } else if (lanecoConsumer.getSCSwitch()) {
            return DoubleManager.rRound(Double.valueOf(((((((((genSys().doubleValue() + tcSystem().doubleValue()) + systemLoss().doubleValue()) + systemLossTransmission()) + icera()) + dcDistribution().doubleValue()) + scSupplySys().doubleValue()) + mcSystem().doubleValue()) + lifelineDiscSubs().doubleValue()) * (-this.rate.getSeniorCitizenDiscount()))).doubleValue();
        } else {
            return 0.0d;
        }
    }

    private double getLifelineDiscount(double kilowattHour) {
        double temp = 0.0d;

        if (!lanecoConsumer.getRateCode().equals("R")) {
            return 0.0d;
        }

        if (kilowattHour < 15.0d) {
            return 0.25d;
        }
        if (kilowattHour == 16.0d && kilowattHour <= 16.0d) {
            return 0.2d;
        }
        if (kilowattHour == 17.0d && kilowattHour <= 17.0d) {
            return 0.1d;
        }
        if (kilowattHour == 18.0d || kilowattHour <= 19.0d) {
            return 0.05d;

        }
        return temp;
    }

    private double getLifelineDiscountRetail(double kilowattHour) {
        double temp = 0.0d;

        if (!lanecoConsumer.getRateCode().equals("R")) {
            return 0.0d;
        }

        if (kilowattHour < 50.0d) {
            return 0.0d;
        }

        return temp;
    }


    public Double businessTax() {
        Log.d("Businestax Temp", String.valueOf(this.rate.getBusinessTax()));
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * this.rate.getBusinessTax()));
    }

    public Double geaAll() {
        Log.d("Gea Al Temp", String.valueOf(this.rate.getBusinessTax()));
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * this.rate.getGeaAll()));
    }

    public Double getRec() {
        Log.d("Gea Rec", String.valueOf(this.rate.getRec()));
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * this.rate.getRec()));
    }

    public Double transmissionSystemCharge() {
        Log.d("Trans nako", String.valueOf(this.rate.getTransmissionSystemCharge()));
        return DoubleManager.rRound(Double.valueOf(getKilowattUsed() * this.rate.getTransmissionSystemCharge()));
    }

    public Double genSys() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * this.rate.getGenSys()));
    }

    public Double hostComm() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getHostComm()));
    }

    public Double tcSystem() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getTcSystem()));
    }

    public Double tcDemand() {
        return DoubleManager.rRound(Double.valueOf(getKilowattUsed() * rate.getTcDemand()));
    }

    public Double ancillaryTransmissionDemandCharge() {
        Log.d("TR Demand NEw1", String.valueOf(getKilowattUsed()));
        Log.d("TR Demand NEw2", String.valueOf( rate.getancillaryTransmissionDemandCharge()));
        return DoubleManager.rRound(Double.valueOf(getKilowattUsed() * rate.getancillaryTransmissionDemandCharge()));
    }

    public Double systemLoss() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * (rate.getSystemLoss() + rate.getSystemLossTransmission())));
    }

    public double systemLossTransmission() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getSystemLossTransmission())).doubleValue();
    }

    public Double icera() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getIcera())).doubleValue();
    }

    public Double dcDistribution() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getDcDistribution()));
    }

    public Double dcDemand() {
        return DoubleManager.rRound(Double.valueOf(getKilowattUsed() * rate.getDcDemand()));
    }

    public Double scSupplySys() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getScSupplySys()));
    }

    public Double mcRetailCust() {

        double mcRetailCust = rate.getMcRetailCust();
        double kwh = getKilowatthour();
        double discount = getLifelineDiscount(kwh);
        double result = mcRetailCust - (mcRetailCust * discount);

        Log.d("BillingCalc",
                "mcRetailCust=" + mcRetailCust +
                        ", kwh=" + kwh +
                        ", discount=" + discount +
                        ", result=" + result);

        return Double.valueOf(rate.getMcRetailCust() - (rate.getMcRetailCust() * getLifelineDiscountRetail(getKilowatthour())));
    }

    public Double mcSystem() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getMcSys()));
    }

    public Double lifelineDiscSubs() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getLifeLineSubsidy()));
    }

    public Double powerActRateRed2() {
        //return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getParr()));
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getParr()));
    }

    public double ucStrandedContractCost() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getUcStrandedContractCost())).doubleValue();
    }

    public double realPropertyTax() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getRealPropertyTax())).doubleValue();
    }


    public double overUnderRecovery() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getOverUnderRecovery())).doubleValue();
    }

    public double vatGensys() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getVatGensys())).doubleValue();
    }

    public double vatHostComm() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getVatHostComm())).doubleValue();
    }

    public double vatTcSystem() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getVatTcSystem())).doubleValue();
    }

    public double vatSystemLoss() {
        Log.d("VatSystemLoss", "lifelineKwhTemp=" + getLifelineKilowatthour()
                + ", vatSystelossTemp=" + rate.getVatSystemLoss()
                + ", result=");

        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getVatSystemLoss())).doubleValue();
    }

    public double vatSystemLossTransmission() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getVatSystemLossTransmission())).doubleValue();
    }

    public double feedTariffAllowance() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getFeedTariffAllowance())).doubleValue();
    }

    public double vatIcera() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getVatIcera())).doubleValue();
    }

    public double vatDcDistribution() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getVatDcDistribution())).doubleValue();
    }

    public double vatScSupply() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getVatScSupply())).doubleValue();
    }

    public double vatMcSystem() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * rate.getVatMcSystem())).doubleValue();
    }

    public double vatPARR() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getVatPARR())).doubleValue();
    }

    public double vatTcDemand() {
        Rates rate = this.dsRates.getConsumerRate(lanecoConsumer.getRateCode());

        return DoubleManager.rRound(Double.valueOf(getKilowattUsed() * rate.getVatTcDemand())).doubleValue();

    }

    public double vatDcDemand() {
        return DoubleManager.rRound(Double.valueOf(getKilowattUsed() * rate.getVatDcDemand())).doubleValue();
    }

    public double vatScRetail() {
        return DoubleManager.rRound(Double.valueOf(rate.getVatScRetail())).doubleValue();
    }

    public double vatMcRetail() {
        return DoubleManager.rRound(Double.valueOf(rate.getVatMcRetail() - (rate.getVatMcRetail() * getLifelineDiscount(getKilowatthour())))).doubleValue();
    }

    public double vatLifelineSubsidy() {
        // return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getVatLifelineSubsidy())).doubleValue();
        return DoubleManager.rRound(Double.valueOf(getKilowatthour())).doubleValue();

    }

    public double vatSeniorCitizen() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getVatSeniorCitizen())).doubleValue();
    }

    public double vatReinvestmentFundSustCapex() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getVatReinvestmentFundSustCapex())).doubleValue();
    }

    public double vatPrevYearAdjPowerCost() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getVatPrevYearAdjPowerCost())).doubleValue();
    }

    public double vatOverUnderRecovery() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getVatOverUnderRecovery())).doubleValue();
    }

    public double regulatedNGCPCharge() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getRegulatedNGCPCharge())).doubleValue();
    }

    public double ancillaryServiceCharge() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getAncillaryServiceCharge())).doubleValue();
    }


    public double ucmeRed() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getUcmeRed())).doubleValue();
    }

    public double totalCharge() {

        Double newTotal = 0.00;
        String isLifeLine = lanecoConsumer.getIsLifeLine();

        // Compute all values once
        double genSysVal = genSys().doubleValue();
        double hostCommVal = hostComm().doubleValue();
        double iceraVal = icera();
        double powerActVal = powerActRateRed2().doubleValue();
        double tcSystemVal = tcSystem().doubleValue();
        double tcDemandVal = tcDemand().doubleValue();
        double systemLossVal = systemLoss().doubleValue();
        double dcDistVal = dcDistribution().doubleValue();
        double dcDemandVal = dcDemand().doubleValue();
        double sysLossTransVal = systemLossTransmission();
        double scSupplyVal = scSupplySys().doubleValue();
        double scRetailVal = scRetailCust().doubleValue();
        double mcSystemVal = mcSystem().doubleValue();
        double mcRetailVal = mcRetailCust().doubleValue();
        double reinvestVal = reinvestmentFundSustCapex().doubleValue();
        double lifelineVal = lifelineDiscSubs().doubleValue();
        double fitVal = feedTariffAllowance();
        double seniorVal = getSeniorCitizenDiscountSubsidy();
        double prevAdjVal = prevYearAdjPowerCost().doubleValue();
        double overUnderVal = overUnderRecovery();
        double ucmeVal = ucme().doubleValue();
        double ucsdVal = ucsd();
        double ucecVal = ucec().doubleValue();
        double strandedVal = ucStrandedContractCost();
        double ucmeRedVal = ucmeRed();
        double rptVal = realPropertyTax();
        double diffVal = lanecoConsumer.getDifferentialBillRecovery();
        double otherVal = lanecoConsumer.getOtherCharges();
        double transformerVal = lanecoConsumer.getTransformerRental();
        double daaVal = lanecoConsumer.getdaaRefund();
        double armatsVal = lanecoConsumer.getArMats();
        double ftVal = FTresult();
        double rptPrevVal = RptPrevTax();
        double locFranVal = locFranTax();
        double transVal = transmissionSystemCharge();
        double businessTaxVal = businessTax();
        double geaVal = geaAll();
        double recVal = getRec();
        double ngcpVal = regulatedNGCPCharge();
        double ancillaryVal = ancillaryServiceCharge();
        double ancillaryDemandVal = ancillaryTransmissionDemandCharge();

        // 🔍 LOG EVERYTHING
        Log.d("TotalCharge", "genSys: " + genSysVal);
        Log.d("TotalCharge", "hostComm: " + hostCommVal);
        Log.d("TotalCharge", "icera: " + iceraVal);
        Log.d("TotalCharge", "powerAct: " + powerActVal);
        Log.d("TotalCharge", "tcSystem: " + tcSystemVal);
        Log.d("TotalCharge", "tcDemand: " + tcDemandVal);
        Log.d("TotalCharge", "systemLoss: " + systemLossVal);
        Log.d("TotalCharge", "dcDistribution: " + dcDistVal);
        Log.d("TotalCharge", "dcDemand: " + dcDemandVal);
        Log.d("TotalCharge", "systemLossTransmission: " + sysLossTransVal);
        Log.d("TotalCharge", "scSupplySys: " + scSupplyVal);
        Log.d("TotalCharge", "scRetailCust: " + scRetailVal);
        Log.d("TotalCharge", "mcSystem: " + mcSystemVal);
        Log.d("TotalCharge", "mcRetailCust: " + mcRetailVal);
        Log.d("TotalCharge", "reinvestmentFund: " + reinvestVal);
        Log.d("TotalCharge", "lifelineDiscSubs: " + lifelineVal);
        Log.d("TotalCharge", "feedTariff: " + fitVal);
        Log.d("TotalCharge", "seniorCitizen: " + seniorVal);
        Log.d("TotalCharge", "prevYearAdj: " + prevAdjVal);
        Log.d("TotalCharge", "overUnderRecovery: " + overUnderVal);
        Log.d("TotalCharge", "ucme: " + ucmeVal);
        Log.d("TotalCharge", "ucsd: " + ucsdVal);
        Log.d("TotalCharge", "ucec: " + ucecVal);
        Log.d("TotalCharge", "ucStranded: " + strandedVal);
        Log.d("TotalCharge", "ucmeRed: " + ucmeRedVal);
        Log.d("TotalCharge", "realPropertyTax: " + rptVal);
        Log.d("TotalCharge", "DifferentialBill: " + diffVal);
        Log.d("TotalCharge", "OtherCharges: " + otherVal);
        Log.d("TotalCharge", "TransformerRental: " + transformerVal);
        Log.d("TotalCharge", "DAARefund: " + daaVal);
        Log.d("TotalCharge", "ArMats: " + armatsVal);
        Log.d("TotalCharge", "FTresult: " + ftVal);
        Log.d("TotalCharge", "RptPrevTax: " + rptPrevVal);
        Log.d("TotalCharge", "LocFranTax: " + locFranVal);
        Log.d("TotalCharge", "Transmission: " + transVal);
        Log.d("TotalCharge", "BusinessTax: " + businessTaxVal);
        Log.d("TotalCharge", "GEA: " + geaVal);
        Log.d("TotalCharge", "REC: " + recVal);
        Log.d("TotalCharge", "NGCP: " + ngcpVal);
        Log.d("TotalCharge", "Ancillary: " + ancillaryVal);
        Log.d("TotalCharge", "AncillaryDemand: " + ancillaryDemandVal);

        // Base total
        double baseTotal =
                genSysVal
                        + hostCommVal
                        + iceraVal
                        + powerActVal
                        + tcSystemVal
                        + tcDemandVal
                        + systemLossVal
                        + dcDistVal
                        + dcDemandVal
                        + sysLossTransVal
                        + scSupplyVal
                        + scRetailVal
                        + mcSystemVal
                        + mcRetailVal
                        + reinvestVal
                        + fitVal
                        + seniorVal
                        + prevAdjVal
                        + overUnderVal
                        + ucmeVal
                        + ucsdVal
                        + ucecVal
                        + strandedVal
                        + ucmeRedVal
                        + rptVal
                        + diffVal
                        + otherVal
                        + transformerVal
                        + daaVal
                        + armatsVal
                        + ftVal
                        + rptPrevVal
                        + locFranVal
                        + transVal
                        + businessTaxVal
                        + geaVal
                        + recVal
                        + ngcpVal
                        + ancillaryVal
                        + ancillaryDemandVal;

        // Lifeline logic
        if ("N".equals(isLifeLine)) {
            newTotal = baseTotal + lifelineVal;
            Log.d("sureball", "TOTAL VAT 1= " + newTotal);

        } else if ("Y".equals(isLifeLine)
                && getKilowatthour() >= 0
                && getKilowatthour() <= 50) {

            newTotal = 0.0;
            Log.d("DEBUG", "kWh final= " + getKilowatthour());
            Log.d("sureball", "TOTAL VAT2 = " + newTotal);
        } else {
            newTotal = baseTotal + lifelineVal;
            Log.d("sureball", "TOTAL VAT 3= " + newTotal);
        }

        Log.d("TotalCharge", "TOTAL regulatedNGCPCharge: " + ngcpVal);
        Log.d("TotalCharge", "TOTAL total charge: " + newTotal);
        Log.d("TotalCharge", "Y or N: " + isLifeLine);

        return newTotal;
    }


    public double totalVat() {
        double gensys = vatGensys();
        double hostComm = vatHostComm();
        double icera = vatIcera();
        double parr = vatPARR();
        double tcSystem = vatTcSystem();
        double tcDemand = vatTcDemand();
        double systemLoss = vatSystemLoss();
        double dcDistribution = vatDcDistribution();
        double dcDemand = vatDcDemand();
        double systemLossTransmission = vatSystemLossTransmission();
        double scSupply = vatScSupply();
        double scRetail = vatScRetail();
        double mcSystem = vatMcSystem();
        double mcRetail = vatMcRetail();
        double reinvestmentFund = vatReinvestmentFundSustCapex();
        double lifelineSubsidy = vatLifelineSubsidy();
        double seniorCitizen = vatSeniorCitizen();
        double prevYearAdj = vatPrevYearAdjPowerCost();
        double overUnderRecovery = vatOverUnderRecovery();

        // ✅ New condition for Rate Code "H"
        double transmissionSystemCharge = 0.0;
        if ("H".equalsIgnoreCase(lanecoConsumer.getRateCode())) {
            transmissionSystemCharge = transmissionSystemCharge();
            Log.d("VATCalculation", "vatTransmissionSystemCharge = " + transmissionSystemCharge);
        }

        // Log each component
        Log.d("VATCalculation", "vatGensys = " + gensys);
        Log.d("VATCalculation", "vatHostComm = " + hostComm);
        Log.d("VATCalculation", "vatIcera = " + icera);
        Log.d("VATCalculation", "vatPARR = " + parr);
        Log.d("VATCalculation", "vatTcSystem = " + tcSystem);
        Log.d("VATCalculation", "vatTcDemand = " + tcDemand);
        Log.d("VATCalculation", "vatSystemLoss = " + systemLoss);
        Log.d("VATCalculation", "vatDcDistribution = " + dcDistribution);
        Log.d("VATCalculation", "vatDcDemand = " + dcDemand);
        Log.d("VATCalculation", "vatSystemLossTransmission = " + systemLossTransmission);
        Log.d("VATCalculation", "vatScSupply = " + scSupply);
        Log.d("VATCalculation", "vatScRetail = " + scRetail);
        Log.d("VATCalculation", "vatMcSystem = " + mcSystem);
        Log.d("VATCalculation", "vatMcRetail = " + mcRetail);
        Log.d("VATCalculation", "vatReinvestmentFundSustCapex = " + reinvestmentFund);
        Log.d("VATCalculation", "vatLifelineSubsidy = " + lifelineSubsidy);
        Log.d("VATCalculation", "vatSeniorCitizen = " + seniorCitizen);
        Log.d("VATCalculation", "vatPrevYearAdjPowerCost = " + prevYearAdj);
        Log.d("VATCalculation", "vatOverUnderRecovery = " + overUnderRecovery);


        double totalVat =
                gensys +
                        hostComm +
                        icera +
                        parr +
                        tcSystem +
                        tcDemand +
                        systemLoss +
                        dcDistribution +
                        dcDemand +
                        systemLossTransmission +
                        scSupply +
                        scRetail +
                        mcSystem +
                        mcRetail +
                        reinvestmentFund +
                        lifelineSubsidy +
                        seniorCitizen +
                        prevYearAdj +
                        overUnderRecovery;

        Log.d("VATCalculation", "TOTAL VAT alll = " + totalVat);
        return gensys
                + hostComm
                + icera
                + parr
                + tcSystem
                + tcDemand
                + systemLoss
                + dcDistribution
                + dcDemand
                + systemLossTransmission
                + scSupply
                + scRetail
                + mcSystem
                + mcRetail
                + reinvestmentFund
                + seniorCitizen
                + prevYearAdj
                + overUnderRecovery
                + transmissionSystemCharge;
    }


    public Double currentBill() {
        return Double.valueOf(totalCharge() + totalVat());
    }

    public double serviceFee() {
        if (lanecoConsumer.getRateCode().equals("I") || lanecoConsumer.getRateCode().equals("H")) {
            if (lanecoConsumer.getRateCode().equals("I")) {
                lanecoConsumer.getClass();
                //  return 19.8d * getKilowattUsed();
                return getKilowattUsed();
            }
            lanecoConsumer.getClass();
            return 0.4d * dcDemand().doubleValue();
        } else if (lanecoConsumer.getRateCode().equals("R")) {
            lanecoConsumer.getClass();
            return 50.0d;
        } else if (lanecoConsumer.getRateCode().equals("C")) {
            lanecoConsumer.getClass();
            return 100.0d;
        } else if (lanecoConsumer.getRateCode().equals("S")) {
            lanecoConsumer.getClass();
            return 50.0d;
        } else if (!lanecoConsumer.getRateCode().equals("P")) {
            return 0.0d;
        } else {
            lanecoConsumer.getClass();
            return 50.0d;
        }
    }

    public double serviceFeeVat() {
        return serviceFee() * 0.12d;
    }

    public double surcharge() {
        return (totalCharge() + totalVat()) * 0.03d;
    }

    public double surchargeVat() {
        return surcharge() * 0.12d;
    }

    public Double amountAfterDue() {
        // return DoubleManager.rRound(Double.valueOf((((currentBill().doubleValue() + serviceFee()) + serviceFeeVat()) + surcharge()) + surchargeVat()));
        return DoubleManager.rRound(Double.valueOf((((currentBill().doubleValue() + serviceFee()) + serviceFeeVat()) + surcharge()) + surchargeVat()));
    }

    public double getKilowattUsed() {
        return reading.getDemand() * lanecoConsumer.getDemandMultiplier(false);
    }

    public double taxableEnergy() {
        return ((((dcDistribution().doubleValue() + scSupplySys().doubleValue()) + mcSystem().doubleValue()) + reinvestmentFundSustCapex().doubleValue()) + lifelineDiscSubs().doubleValue()) + getSeniorCitizenDiscountSubsidy();
    }

    public double totalDsm() {
        return 0;
        //((((dcDistribution().doubleValue() + dcDemand().doubleValue() + scSupplySys().doubleValue()) + scRetailCust().doubleValue() + mcSystem().doubleValue()) + mcRetailCust().doubleValue()) + lifelineDiscSubs().doubleValue()) + getSeniorCitizenDiscountSubsidy();
    }

    public double FTresult() {
        return totalDsm() * lanecoConsumer.gettracTax();
    }

    public double RptPrevTax() {
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * lanecoConsumer.getrptprevTax()));
    }

    public double locFranTax() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * lanecoConsumer.getlocalFranchiseTax()));
    }
}
