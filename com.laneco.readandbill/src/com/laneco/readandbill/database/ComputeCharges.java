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


    public Double businessTax() {
        Log.d("Businestax Temp", String.valueOf(this.rate.getBusinessTax()));
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * this.rate.getBusinessTax()));
    }

    public Double geaAll() {
        Log.d("Gea Al Temp", String.valueOf(this.rate.getBusinessTax()));
        return DoubleManager.rRound(Double.valueOf(getLifelineKilowatthour() * this.rate.getGeaAll()));
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
        return Double.valueOf(rate.getMcRetailCust() - (rate.getMcRetailCust() * getLifelineDiscount(getKilowatthour())));
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

    public double ucmeRed() {
        return DoubleManager.rRound(Double.valueOf(getKilowatthour() * rate.getUcmeRed())).doubleValue();
    }

    public double totalCharge() {
        Log.d("TotalCharge", "genSys: " + genSys().doubleValue());
        Log.d("TotalCharge", "hostComm: " + hostComm().doubleValue());
        Log.d("TotalCharge", "icera: " + icera());
        Log.d("TotalCharge", "powerActRateRed2: " + powerActRateRed2().doubleValue());
        Log.d("TotalCharge", "tcSystem: " + tcSystem().doubleValue());
        Log.d("TotalCharge", "tcDemand: " + tcDemand().doubleValue());
        Log.d("TotalCharge", "systemLoss: " + systemLoss().doubleValue());
        Log.d("TotalCharge", "dcDistribution: " + dcDistribution().doubleValue());
        Log.d("TotalCharge", "dcDemand: " + dcDemand().doubleValue());
        Log.d("TotalCharge", "systemLossTransmission: " + systemLossTransmission());
        Log.d("TotalCharge", "scSupplySys: " + scSupplySys().doubleValue());
        Log.d("TotalCharge", "scRetailCust: " + scRetailCust().doubleValue());
        Log.d("TotalCharge", "mcSystem: " + mcSystem().doubleValue());
        Log.d("TotalCharge", "mcRetailCust: " + mcRetailCust().doubleValue());
        Log.d("TotalCharge", "reinvestmentFundSustCapex: " + reinvestmentFundSustCapex().doubleValue());
        Log.d("TotalCharge", "lifelineDiscSubs: " + lifelineDiscSubs().doubleValue());
        Log.d("TotalCharge", "feedTariffAllowance: " + feedTariffAllowance());
        Log.d("TotalCharge", "seniorCitizenDiscountSubsidy: " + getSeniorCitizenDiscountSubsidy());
        Log.d("TotalCharge", "prevYearAdjPowerCost: " + prevYearAdjPowerCost().doubleValue());
        Log.d("TotalCharge", "overUnderRecovery: " + overUnderRecovery());
        Log.d("TotalCharge", "ucme: " + ucme().doubleValue());
        Log.d("TotalCharge", "ucsd: " + ucsd());
        Log.d("TotalCharge", "ucec: " + ucec().doubleValue());
        Log.d("TotalCharge", "ucStrandedContractCost: " + ucStrandedContractCost());
        Log.d("TotalCharge", "ucmeRed: " + ucmeRed());
        Log.d("TotalCharge", "realPropertyTax: " + realPropertyTax());
        Log.d("TotalCharge", "differentialBillRecovery: " + lanecoConsumer.getDifferentialBillRecovery());
        Log.d("TotalCharge", "otherCharges: " + lanecoConsumer.getOtherCharges());
        Log.d("TotalCharge", "transformerRental: " + lanecoConsumer.getTransformerRental());
        Log.d("TotalCharge", "daaRefund: " + lanecoConsumer.getdaaRefund());
        Log.d("TotalCharge", "arMats: " + lanecoConsumer.getArMats());
        Log.d("TotalCharge", "FTresult: " + FTresult());
        Log.d("TotalCharge", "RptPrevTax: " + RptPrevTax());
        Log.d("TotalCharge", "locFranTax: " + locFranTax());
        Log.d("TotalCharge", "businesstax: " + businessTax());
        Log.d("TotalCharge", "transmission system charge: " + transmissionSystemCharge());
        Double newTotal = 0.00;

        Log.d("Life line ni siya ", "if life line siya: " + rate.getIsLifeLine().toString());
        Log.d("Account nymber ", "if life line siya: " + lanecoConsumer.getName());
        if (rate.isLifeLine.toString().equals("N")) {
            newTotal = ((((((((((((((((((((((((((((genSys().doubleValue()
                    + hostComm().doubleValue())
                    + icera())
                    + powerActRateRed2().doubleValue())
                    + tcSystem().doubleValue())
                    + tcDemand().doubleValue())
                    + systemLoss().doubleValue())
                    + dcDistribution().doubleValue())
                    + dcDemand().doubleValue())
                    + systemLossTransmission())
                    + scSupplySys().doubleValue())
                    + scRetailCust().doubleValue())
                    + mcSystem().doubleValue())
                    + mcRetailCust().doubleValue())
                    + reinvestmentFundSustCapex().doubleValue())
                    + lifelineDiscSubs().doubleValue())
                    + feedTariffAllowance())
                    + getSeniorCitizenDiscountSubsidy())
                    + prevYearAdjPowerCost().doubleValue())
                    + overUnderRecovery()) + ucme().doubleValue())
                    + ucsd())
                    + ucec().doubleValue())
                    + ucStrandedContractCost())
                    + ucmeRed())
                    + realPropertyTax())
                    + this.lanecoConsumer.getDifferentialBillRecovery())
                    + this.lanecoConsumer.getOtherCharges())
                    + this.lanecoConsumer.getTransformerRental())
                    + this.lanecoConsumer.getdaaRefund()
                    + this.lanecoConsumer.getArMats()
                    + FTresult()
                    + RptPrevTax()
                    + locFranTax()
                    + transmissionSystemCharge()
                    +  businessTax()
                    + geaAll();
        } if ("Y".equals(rate.getIsLifeLine())
                && getKilowattUsed() >= 0
                && getKilowattUsed() <= 19) {

            newTotal = 0.0;
        }

        else {
            newTotal =
                    genSys().doubleValue()
                            + hostComm().doubleValue()
                            + icera()
                            + powerActRateRed2().doubleValue()
                            + tcSystem().doubleValue()
                            + tcDemand().doubleValue()
                            + systemLoss().doubleValue()
                            + dcDistribution().doubleValue()
                            + dcDemand().doubleValue()
                            + systemLossTransmission()
                            + scSupplySys().doubleValue()
                            + scRetailCust().doubleValue()
                            + mcSystem().doubleValue()
                            + mcRetailCust().doubleValue()
                            + reinvestmentFundSustCapex().doubleValue()
                            + feedTariffAllowance()
                            + getSeniorCitizenDiscountSubsidy()
                            + prevYearAdjPowerCost().doubleValue()
                            + overUnderRecovery()
                            + ucme().doubleValue()
                            + ucsd()
                            + ucec().doubleValue()
                            + ucStrandedContractCost()
                            + ucmeRed()
                            + realPropertyTax()
                            + this.lanecoConsumer.getDifferentialBillRecovery()
                            + this.lanecoConsumer.getOtherCharges()
                            + this.lanecoConsumer.getTransformerRental()
                            + this.lanecoConsumer.getdaaRefund()
                            + this.lanecoConsumer.getArMats()
                            + FTresult()
                            + RptPrevTax()
                            + locFranTax()
                            + transmissionSystemCharge() + geaAll()+  businessTax();

        }

        Log.d("TotalCharge", "TOTAL OVeral: " + newTotal);
        Log.d("Y or N: ",   rate.getIsLifeLine().toString());
        return newTotal;
//        return ((((((((((((((((((((((((((((genSys().doubleValue() + hostComm().doubleValue()) + icera()) + powerActRateRed2().doubleValue()) + tcSystem().doubleValue()) + tcDemand().doubleValue()) + systemLoss().doubleValue()) + dcDistribution().doubleValue()) + dcDemand().doubleValue()) + systemLossTransmission()) + scSupplySys().doubleValue()) + scRetailCust().doubleValue()) + mcSystem().doubleValue()) + mcRetailCust().doubleValue()) + reinvestmentFundSustCapex().doubleValue()) + lifelineDiscSubs().doubleValue()) + feedTariffAllowance()) + getSeniorCitizenDiscountSubsidy()) + prevYearAdjPowerCost().doubleValue()) + overUnderRecovery()) + ucme().doubleValue()) + ucsd()) + ucec().doubleValue()) + ucStrandedContractCost()) + ucmeRed()) + realPropertyTax()) + this.lanecoConsumer.getDifferentialBillRecovery()) + this.lanecoConsumer.getOtherCharges()) + this.lanecoConsumer.getTransformerRental()) + this.lanecoConsumer.getdaaRefund() + this.lanecoConsumer.getArMats() + FTresult() + RptPrevTax() + locFranTax() + businessTax();
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
