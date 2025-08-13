package com.laneco.readandbill;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import com.androidapp.mytools.bluetooth.PrinterControls;
import com.androidapp.mytools.objectmanager.StringManager;
import com.laneco.readandbill.database.ComputeCharges;
import com.laneco.readandbill.database.Consumer;
import com.laneco.readandbill.database.RateDataSource;
import com.laneco.readandbill.database.Rates;
import com.laneco.readandbill.database.Reading;
import com.laneco.readandbill.database.UserProfile;
import com.laneco.readandbill.database.UserProfileDataSource;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatementGenerator {
    protected DecimalFormat amountFormat;
    private ComputeCharges compute;
    private Consumer consumer;
    protected DecimalFormat kilowattUsed;
    protected DecimalFormat percentage;
    private Rates rate;
    protected DecimalFormat rateFormat;
    private Reading reading;
    protected DecimalFormat totalAmountFormat;
    private UserProfile userProfile;
    private DecimalFormat vatFormat;

    public StatementGenerator(Context context, Consumer myConsumer, Reading reading) {
        RateDataSource dsRate = new RateDataSource(context);
        UserProfileDataSource dsUserProfile = new UserProfileDataSource(context);
        this.consumer = myConsumer;
        this.compute = new ComputeCharges(context, myConsumer);
        if (this.consumer != null) {
            this.rate = dsRate.getConsumerRate(this.consumer.getRateCode());
        }
        this.userProfile = dsUserProfile.getUserProfile();
        this.kilowattUsed = new DecimalFormat("###0.0");
        this.percentage = new DecimalFormat("##0");
        this.amountFormat = new DecimalFormat("###,##0.00");
        this.totalAmountFormat = new DecimalFormat("###,###,##0.00");
        this.rateFormat = new DecimalFormat("0.0000");
        this.vatFormat = new DecimalFormat("##,##0.00");
        this.reading = reading;
        this.compute.setReading(reading);
    }

    public List<String> generateSOA() {
        List<String> result = new ArrayList();
        for (String string : soaHeader()) {
            result.add(string);
        }
        for (String string2 : soaBody()) {
            result.add(string2);
        }
        for (String string22 : soaFooter()) {
            result.add(string22);
        }
        return result;
    }

    protected List<String> soaHeader() {
        List<String> result = new ArrayList();
        result.add(PrinterControls.char48());
        if (PrinterControls.btPrinter.getDeviceName().equals("SPP-R300")) {
            result.add("\u001b31");
        }
        result.add("Ver 2.2.0.1\n");
        result.add(PrinterControls.emphasized(true));
        result.add(StringManager.centerJustify("LANAO DEL NORTE ELECTRIC COOPERATIVE, INC.", 48) + "\n");
        result.add(PrinterControls.emphasized(false));
        result.add(StringManager.centerJustify("Tubod, Lanao del Norte, Mindanao, Philippines", 48) + "\n");
        result.add(StringManager.centerJustify("VAT REG. TIN 000-954-478-000", 48) + "\n");
        result.add(StringManager.centerJustify("TEL. NO. (063)341-5231 FAX NO. (063)341-5210", 48) + "\n");
        result.add('\035' + StringManager.centerJustify("E-MAIL: laneco_energy@yahoo.com", 48) + "\n");
        result.add(PrinterControls.emphasized(true));
        result.add('\035' + StringManager.centerJustify(" BILLING INVOICE", 48) + "\n");
        result.add(PrinterControls.emphasized(false));
        result.add(StringManager.centerJustify(this.userProfile.getBillingPeriod(), 48) + "\n");
        result.add(StringManager.centerJustify("Billing Period " + this.userProfile.getInitialReadingDate() + " to " + this.userProfile.getReadingDate(), 48) + "\n");
        result.add(StringManager.leftJustify(StringManager.leftJustify(this.consumer.getAccountNumber(), 10) + StringManager.centerJustify("TYPE - " + this.consumer.getRateCode(), 14) + StringManager.leftJustify("DUE DATE: ", 10) + StringManager.rightJustify(this.userProfile.getDueDate(), 14), 48) + "\n");
        result.add(StringManager.leftJustify(StringManager.leftJustify(this.consumer.getName(), 24) + StringManager.leftJustify("Energized:", 10) + StringManager.rightJustify(this.consumer.getDateEnergized(), 14), 48) + "\n");
        result.add(StringManager.leftJustify(StringManager.leftJustify(this.consumer.getMeterSerial() + " Brand " + this.consumer.getMeterBrand(), 24) + StringManager.leftJustify("POLE No. ", 10) + StringManager.rightJustify(this.consumer.getPoleNumber(), 14), 48) + "\n");
        result.add(StringManager.leftJustify(StringManager.leftJustify("Ave KWHr ", 10) + StringManager.rightJustify(String.valueOf(this.consumer.getAveKwh()) + " ", 14) + StringManager.leftJustify("TransLoss ", 10) + StringManager.rightJustify(String.valueOf(this.consumer.getTransLoss()), 14), 48) + "\n");
        result.add(lineBreak(48));
        for (String string : readingDetail()) {
            result.add(string);
        }
        return result;
    }

    private List<String> readingDetail() {
        List<String> result = new ArrayList();
        result.add(StringManager.rightJustify("PRES READING", 14) + StringManager.leftJustify(StringManager.rightJustify("PREV READING", 14) + StringManager.rightJustify("MULT.", 6) + StringManager.rightJustify("KWH USED", 14), 48) + "\n");
        if (this.reading.getFeedBackCode().equals("A")) {
            result.add(StringManager.rightJustify(String.valueOf(this.consumer.getInitialReading()), 13) + " " +
                    StringManager.leftJustify(StringManager.rightJustify(String.valueOf(this.consumer.getInitialReading()), 13) + " " +
                            StringManager.rightJustify(String.valueOf(this.consumer.getMultiplier()), 5) + " " + StringManager.rightJustify(String.valueOf(this.compute.getKilowatthour()), 14), 48) + "\n");
        } else {
            result.add(
                    StringManager.rightJustify(String.valueOf(this.reading.getReading()), 13) + " " +
                            StringManager.leftJustify(
                                    StringManager.rightJustify(String.valueOf(this.consumer.getInitialReading()), 13) + " " +
                                            StringManager.rightJustify(String.valueOf(this.consumer.getMultiplier()), 5) + " " +
                                            StringManager.rightJustify(
                                                    String.valueOf(Math.round(this.reading.getReading() - this.consumer.getInitialReading())), 14
                                            ),
                                    48
                            ) + "\n"
            );


        }
        if (this.consumer.getChangeMeterKilowatthour() > 0.0d) {
            result.add("with Change Meter");
            result.add(String.valueOf(this.consumer.getChangeMeterKilowatthour()) + "\n");
        }
        result.add(StringManager.leftJustify(StringManager.rightJustify("LFL KWHr", 14) + StringManager.rightJustify("DEM. READING", 14) + StringManager.rightJustify("MULT.", 6) + StringManager.rightJustify("KILOWATT USED", 14), 48) + "\n");
        result.add(StringManager.leftJustify(StringManager.rightJustify(String.valueOf(this.compute.getLifelineKilowatthourDisplay()), 13) + " " + StringManager.rightJustify(String.valueOf(this.reading.getDemand()), 13) + " " + StringManager.rightJustify(String.valueOf(this.consumer.getDemandMultiplier(true)), 5) + " " + StringManager.rightJustify(String.valueOf(this.compute.getKilowattUsed()), 14), 48) + "\n");
        return result;
    }

    // new calculation for 2025 no lifline

    protected List<String> soaBody() {
        List<String> result = new ArrayList();
        result.add(lineBreak(48));
        result.add(StringManager.leftJustify("", 30) + StringManager.centerJustify("RATE", 7) + StringManager.centerJustify("AMOUNT", 11) + "\n");
        result.add(PrinterControls.emphasized(true));
        result.add("GENERATION AND TRANSMISSION\n");
        result.add(PrinterControls.emphasized(false));
        if (this.compute.genSys().doubleValue() != 0.0d) {
           // result.add(bodyLineGenerator("Generation System Charge", this.rate.getGenSys(), this.compute.genSys().doubleValue()) + "\n");
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double gentran = tempTotal * this.rate.getGenSys();
            result.add(bodyLineGenerator("Generation System Charge", this.rate.getGenSys(),   gentran) + "\n");
        }
        if (this.compute.hostComm().doubleValue() != 0.0d) {
            double gen = this.compute.getKilowatthour() * this.rate.getGenSys();
            result.add(bodyLineGenerator("Franchise & Ben. to Host Comm", this.rate.getHostComm(), this.compute.hostComm().doubleValue()) + "\n");

        }
        if (this.compute.icera().doubleValue() != 0.0d) {
            result.add(bodyLineGenerator("ICERA", this.rate.getIcera(), this.compute.icera().doubleValue()) + "\n");
        }
        if (this.compute.powerActRateRed2().doubleValue() != 0.0d) {
           // result.add(bodyLineGenerator("Power Act Reduction", this.rate.getParr(), this.compute.powerActRateRed2().doubleValue()) + "\n");
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double par = tempTotal * this.rate.getParr();
            result.add(bodyLineGenerator("Power Act Reduction", this.rate.getParr(), par) + "\n");
        }
        if (this.compute.tcSystem().doubleValue() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());


            double trans = tempTotal * this.rate.getTcSystem();
          //  result.add(bodyLineGenerator("Transmission System Charge", this.rate.getTcSystem(), this.compute.tcSystem().doubleValue()) + "\n");
            result.add(bodyLineGenerator("Transmission System Charge", this.rate.getTcSystem(), trans) + "\n");
        }
        if (this.compute.tcDemand().doubleValue() != 0.0d) {
            result.add(bodyLineGenerator("Transmission Dem. Charge", this.rate.getTcDemand(), this.compute.tcDemand().doubleValue()) + "\n");
        }
        if (this.compute.systemLoss().doubleValue() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());

            double systemLoss = tempTotal * this.rate.getSystemLoss();
           /// result.add(bodyLineGenerator("System Loss Charge", this.rate.getSystemLoss(), this.compute.systemLoss().doubleValue()) + "\n");
            result.add(bodyLineGenerator("System Loss Charge", this.rate.getSystemLoss(), systemLoss) + "\n");
        }
        if (this.consumer.getdaaRefund() != 0.0d) {
            result.add(bodyLineGenerator("DAA REFUND", this.consumer.getdaaRefund()) + "\n");
        }
        result.add(PrinterControls.emphasized(true));
        result.add("DISTRIBUTION REVENUES\n");
        result.add(PrinterControls.emphasized(false));
        if (this.compute.dcDistribution().doubleValue() != 0.0d) {

            double tempTotal =Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double disSystemCharge = tempTotal * this.rate.getDcDistribution();
            result.add(bodyLineGenerator("Distribution System Charge", this.rate.getDcDistribution(), disSystemCharge) + "\n");
        }
        if (this.compute.dcDemand().doubleValue() != 0.0d) {
            result.add(bodyLineGenerator("Distribution Dem. Charge", this.rate.getDcDemand(), this.compute.dcDemand().doubleValue()) + "\n");
        }
        if (this.compute.scSupplySys().doubleValue() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double supplySystem = tempTotal * this.rate.getScSupplySys();
            result.add(bodyLineGenerator("Supply System Charge", this.rate.getScSupplySys(), supplySystem) + "\n");
        }
        if (this.compute.scRetailCust().doubleValue() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double retailSupply = tempTotal * this.rate.getScRetailCust();
          //  result.add(bodyLineGenerator("Supply Retail Cust. Charge", this.rate.getScRetailCust(), this.compute.scRetailCust().doubleValue()) + "\n");
            result.add(bodyLineGenerator("Supply Retail Cust. Charge", retailSupply) + "\n");
        }
        if (this.compute.mcSystem().doubleValue() != 0.0d) {
            //result.add(bodyLineGenerator("Metering System Charge", this.rate.getMcSys(), this.compute.mcSystem().doubleValue()) + "\n");
            double tempTotal =Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double metringSystem = tempTotal * this.rate.getMcSys();
            result.add(bodyLineGenerator("Metering System Charge", this.rate.getMcSys(), metringSystem) + "\n");
        }
        if (this.compute.mcRetailCust().doubleValue() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double total = tempTotal * this.rate.getMcRetailCust();

            if (!"R".equalsIgnoreCase(this.consumer.getRateCode())) {
                result.add(bodyLineGenerator("Metering Retail Customer", this.rate.getMcRetailCust(), this.compute.mcRetailCust().doubleValue()) + "\n");
            }
            else{
                result.add(bodyLineGenerator("Metering Retail Customer", this.rate.getMcRetailCust(), this.rate.getMcRetailCust()) + "\n");
            }
            //result.add(bodyLineGenerator("Metering Retail Customer", this.rate.getMcRetailCust(), this.compute.mcRetailCust().doubleValue()) + "\n");

        }
        if (this.compute.reinvestmentFundSustCapex().doubleValue() != 0.0d) {
           // result.add(bodyLineGenerator("Reinvest. Fund For Sust.CAPEX", this.rate.getReinvestmentFundSustCapex(), this.compute.reinvestmentFundSustCapex().doubleValue()) + "\n");
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double total = tempTotal * this.rate.getReinvestmentFundSustCapex();
            result.add(bodyLineGenerator("Reinvest. Fund For Sust.CAPEX", this.rate.getReinvestmentFundSustCapex(), total) + "\n");

        }
        result.add(PrinterControls.emphasized(true));
        result.add(StringManager.leftJustify("OTHERS", 48) + "\n");
        result.add(PrinterControls.emphasized(false));
//        if (this.compute.lifelineDiscSubs().doubleValue() != 0.0d) {
//            result.add(bodyLineGenerator("LifeLine (Discount) Subsidy", this.rate.getLifeLineSubsidy(), this.compute.lifelineDiscSubs().doubleValue()) + "\n");
//        }
        if (this.compute.getSeniorCitizenDiscountSubsidy() != 0.0d) {
            if (!this.consumer.getSCSwitch()) {
                double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
                double total = tempTotal * this.rate.getSeniorCitizenSubsidy();
               // result.add(bodyLineGenerator("Senior Citizen (Disc.) Subs.", this.rate.getSeniorCitizenSubsidy(), this.compute.getSeniorCitizenDiscountSubsidy()) + "\n");
                result.add(bodyLineGenerator("Senior Citizen (Disc.) Subs.", this.rate.getSeniorCitizenSubsidy(), total) + "\n");
            } else if (this.consumer.getSCSwitch()) {
                double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
                double total = tempTotal * this.rate.getSeniorCitizenDiscount();
             //   result.add(bodyLineGenerator("Senior Citizen (Disc.) Subs.", this.consumer.getSeniorCitizenDiscount(), this.compute.getSeniorCitizenDiscountSubsidy()) + "\n");
                result.add(bodyLineGenerator("Senior Citizen (Disc.) Subs.", this.consumer.getSeniorCitizenDiscount(), total) + "\n");

            }
        }
        if (this.compute.prevYearAdjPowerCost().doubleValue() != 0.0d) {
            result.add(bodyLineGenerator("Previous Year Adjustment", this.rate.getPrevYearAdjPowerCost(), this.compute.prevYearAdjPowerCost().doubleValue()) + "\n");
        }
        if (this.compute.overUnderRecovery() != 0.0d) {
            result.add(bodyLineGenerator("(Over) / Under Recoveries", this.rate.getOverUnderRecovery(), this.compute.overUnderRecovery()) + "\n");
        }
        result.add(PrinterControls.emphasized(true));
        result.add("GOVERMENT REVENUES\n");
        result.add(PrinterControls.emphasized(false));
        if (this.compute.realPropertyTax() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double total = tempTotal * this.rate.getRealPropertyTax();
           // result.add(bodyLineGenerator("Real Property Tax", this.rate.getRealPropertyTax(), this.compute.realPropertyTax()) + "\n");
            result.add(bodyLineGenerator("Real Property Tax", this.rate.getRealPropertyTax(),total) + "\n");
        }
        long diff = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
        double taxnew= Math.round(diff * 0.0057 * 100.0) / 100.0;

        result.add(bodyLineGenerator("\nBusiness Tax Yr. 2024-25:  ", 0.0057, taxnew) + "\n");



        if (this.compute.ucme().doubleValue() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double total = tempTotal * this.rate.getUcme();
           // result.add(bodyLineGenerator("UC-ME (NPC-SPUG)", this.rate.getUcme(), this.compute.ucme().doubleValue()) + "\n");
            result.add(bodyLineGenerator("UC-ME (NPC-SPUG)", this.rate.getUcme(), total) + "\n");

        }
        if (this.compute.ucec().doubleValue() != 0.0d) {
            result.add(bodyLineGenerator("Environmental Charge", this.rate.getUcec(), this.compute.ucec().doubleValue()) + "\n");
        }
        if (this.compute.ucStrandedContractCost() != 0.0d) {
            result.add(bodyLineGenerator("Stranded Contract Cost", this.rate.getUcStrandedContractCost(), this.compute.ucStrandedContractCost()) + "\n");
        }
        if (this.compute.ucmeRed() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double total = tempTotal * this.rate.getUcmeRed();
           // result.add(bodyLineGenerator("UC-ME (RED)", this.rate.getUcmeRed(), this.compute.ucmeRed()) + "\n");
            result.add(bodyLineGenerator("UC-ME (RED)", this.rate.getUcmeRed(),total) + "\n");
        }
        //UCSD Charge
        if (this.compute.ucsd() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double total = tempTotal * this.rate.getUcsd();
            //result.add(bodyLineGenerator("UCSD Charge", this.rate.getUcsd(), this.compute.ucsd()) + "\n");
            result.add(bodyLineGenerator("UCSD Charge", this.rate.getUcsd(), total) + "\n");
        }
        if (this.compute.feedTariffAllowance() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double total = tempTotal * this.rate.getFeedTariffAllowance();
           // result.add(bodyLineGenerator("Fit-All (Renewable)", this.rate.getFeedTariffAllowance(), this.compute.feedTariffAllowance()) + "\n");
            result.add(bodyLineGenerator("Fit-All (Renewable)", this.rate.getFeedTariffAllowance(), total) + "\n");
        }
        if (this.consumer.getDifferentialBillRecovery() != 0.0d) {
            result.add(bodyLineGenerator("Differential Billing/Recover", this.consumer.getDifferentialBillRecovery()) + "\n");
        }
        if (this.consumer.getOtherCharges() != 0.0d) {
            result.add(bodyLineGenerator("Other Charges (456/142/421)", this.consumer.getOtherCharges()) + "\n");
        }
        if (this.consumer.getArMats() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double total = tempTotal * this.consumer.getArMats();
           result.add(bodyLineGenerator("A/R (Materials)", this.consumer.getArMats()) + "\n");
          //  result.add(bodyLineGenerator("A/R (Materials)", total) + "\n");
        }
        if (this.consumer.getTransformerRental() != 0.0d) {
            result.add(bodyLineGenerator("Transformer Rental", this.consumer.getTransformerRental()) + "\n");
        }
        if (this.compute.totalDsm() != 0.0d) {
            result.add(bodyLineGenerator("Franchise Tax", this.consumer.gettracTax(), this.compute.FTresult()) + "\n");
        }
        if (this.compute.locFranTax() != 0.0d) {
            double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
            double total = tempTotal * this.consumer.getlocalFranchiseTax();
            //result.add(bodyLineGenerator("Local Franchise Tax", this.consumer.getlocalFranchiseTax(), this.compute.locFranTax()) + "\n");
            result.add(bodyLineGenerator("Local Franchise Tax", this.consumer.getlocalFranchiseTax(), total) + "\n");

        }
        if (this.compute.RptPrevTax() != 0.0d) {
            result.add(bodyLineGenerator("RPT Previous Year", this.consumer.getrptprevTax(), this.compute.RptPrevTax()) + "\n");
        }

        if (this.compute.totalVat() != 0.0d) {
            //double tempTotal = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
          //  double total = tempTotal * this.compute.totalVat();
           // result.add(bodyLineGenerator("Vat amount", this.compute.totalVat()) + "\n");
            double tempTotalVat = this.reading.getReading() - this.consumer.getInitialReading();
            int roundedTempTotal = (int) Math.round(tempTotalVat);
            double tempTotal = roundedTempTotal;
            double totalChargeVats = 0.00;

            //residential
            double generationChargeRate = 0;
            double transmissionChargeRate = 0;
            double demandChargeRate = 0;
            double supplyChargeRate =0;
            double meteringChargeRate = 0;
            double meteringSystemCharge = 0;
            double seniorCitizenCharge = 0;
            double parAdjustment = 0;
            double slGenerationCharge = 0;
            double slTransmissionCharge = 0;


            //Commercial
            double generationChargeRateCommercial = 0;
            double powerActReductionCommercial = 0;
            double transmissionSystemChargeCommercial = 0;
            double systemLossChargeCommercial = 0;
            double distributionSystemChargeCommercial = 0;
            double supplySystemChargeCommercial = 0;
            double meteringSystemChargeCommercial = 0;
            double meteringRetailCustomerCommercial = 0;
            double supplyRetailCommercial = 0;
            double reinvestmentFundForCapexCommercial = 0;
            double realPropertyTaxCommercial = 0;
            double businessTaxCommercial = 0;
            double ucMeNpcSpugCommercial = 0;
            double ucMeRedCommercial = 0;
            double ucsdChargeCommercial = 0;
            double fitAllRenewableCommercial = 0;
            double localFranchiseTaxCommercial = 0;



            if ("P".equalsIgnoreCase(this.consumer.getRateCode()) || "R".equalsIgnoreCase(this.consumer.getRateCode())) {
                 generationChargeRate = 0.5335 * tempTotal;
                 transmissionChargeRate = 0.1338 * tempTotal;
                 demandChargeRate = 0.1014 * tempTotal;
                 supplyChargeRate = 0.0928 * tempTotal;
                 meteringChargeRate = 0.0548 * tempTotal;
                 meteringSystemCharge = 0.6 ;
                 seniorCitizenCharge = 0.0001 * tempTotal;
                 parAdjustment = -0.0147 * tempTotal;
                 slGenerationCharge = 0.0671 * tempTotal;
                 slTransmissionCharge = 0.0136 * tempTotal;



                totalChargeVats = generationChargeRate + transmissionChargeRate
                        + demandChargeRate + supplyChargeRate + meteringChargeRate
                        + meteringSystemCharge + seniorCitizenCharge + parAdjustment
                        + slGenerationCharge+slTransmissionCharge;
                result.add(bodyLineGenerator("Vat amounts", totalChargeVats) + "\n");

            }

            if ("C".equalsIgnoreCase(this.consumer.getRateCode()) ) {



                generationChargeRateCommercial       = 5.8599 * tempTotal;
                powerActReductionCommercial          = -0.1229 * tempTotal;
                transmissionSystemChargeCommercial   = 1.1597 * tempTotal;
                systemLossChargeCommercial           = 0.8642 * tempTotal;
                distributionSystemChargeCommercial   = 0.8449 * tempTotal;
                supplySystemChargeCommercial         = 0.7732 * tempTotal;
                meteringSystemChargeCommercial       = 0.4569 * tempTotal;
                meteringRetailCustomerCommercial     = 28.7200 * tempTotal;
                supplyRetailCommercial               = 40.1500 * tempTotal;
                reinvestmentFundForCapexCommercial   = 0.5189 * tempTotal;
                realPropertyTaxCommercial            = 0.0227 * tempTotal;
                businessTaxCommercial                = 0.0057 * tempTotal;
                ucMeNpcSpugCommercial                = 0.1949 * tempTotal;
                ucMeRedCommercial                    = 0.0044 * tempTotal;
                ucsdChargeCommercial                 = 0.0428 * tempTotal;
                fitAllRenewableCommercial            = 0.1189 * tempTotal;
                localFranchiseTaxCommercial          = 0.0104 * tempTotal;
                totalChargeVats=
                        generationChargeRateCommercial +
                                powerActReductionCommercial +
                                transmissionSystemChargeCommercial +
                                systemLossChargeCommercial +
                                distributionSystemChargeCommercial +
                                supplySystemChargeCommercial +
                                meteringSystemChargeCommercial +
                                meteringRetailCustomerCommercial +
                                supplyRetailCommercial +
                                reinvestmentFundForCapexCommercial +
                                realPropertyTaxCommercial +
                                businessTaxCommercial +
                                ucMeNpcSpugCommercial +
                                ucMeRedCommercial +
                                ucsdChargeCommercial +
                                fitAllRenewableCommercial +
                                localFranchiseTaxCommercial;


                double generationChargeRateCommercialVat = 0;
                double transmissionSystemChargeCommercialVat = 0;
                double demandChargePerKwCommercialVat = 0;
                double supplySystemCharge2CommercialVat = 0;
                double meteringChargesCommercialVat = 0;
                double meteringSystemChargeCommercialVat = 0;
                double seniorCitizenSubsidyChargeCommercialVat = 0;
                double powerActReductionAdjCommercialVat = 0;
                double slGenerationCommercialVat = 0;
                double slTransmissionCommercialVat = 0;

// Assign values
                generationChargeRateCommercialVat         = 0.5335 * tempTotal;
                transmissionSystemChargeCommercialVat     = 0.1338 * tempTotal;
                demandChargePerKwCommercialVat             = 0.1014 * tempTotal;
                supplySystemCharge2CommercialVat           = 0.0928 * tempTotal;
                meteringChargesCommercialVat               = 0.0548 * tempTotal;
                meteringSystemChargeCommercialVat         = 8.2644 ;
                seniorCitizenSubsidyChargeCommercialVat    = 0.0001 * tempTotal;
                powerActReductionAdjCommercialVat          = -0.0147 * tempTotal;
                slGenerationCommercialVat                  = 0.0671 * tempTotal;
                slTransmissionCommercialVat                = 0.0136 * tempTotal;


                totalChargeVats = generationChargeRate + transmissionChargeRate
                        + demandChargeRate + supplyChargeRate + meteringChargeRate
                        + meteringSystemCharge + seniorCitizenCharge + parAdjustment
                        + slGenerationCharge+slTransmissionCharge;
                result.add(bodyLineGenerator("Vat amounts", totalChargeVats) + "\n");
            }

            if ("H".equalsIgnoreCase(this.consumer.getRateCode()) ) {
                double generationChargeH1 = 0.6174;
                double transmissionSystemChargeH1 = 0.031;
                double systemLossChargeH1 = 0.0914;
                double distributionChargeH1 = 32.15;
                double supplySystemChargeH1 = 4.818;
                double meteringChargesH1 = 3.4466;
                double lifelineRateDiscountH1 = 0.0084;
                double mccRfscH1 = 0.0623;

                double purchaceEnergy =tempTotal*420.00;
                double demand =420*0.889;

                double generationChargeHT = generationChargeH1 * purchaceEnergy;
                double transmissionSystemChargeHT = transmissionSystemChargeH1 * demand;
                double systemLossChargeHT = systemLossChargeH1 * purchaceEnergy;
                double distributionChargeHT = distributionChargeH1 * demand;
                double supplySystemChargeHT = supplySystemChargeH1;
                double meteringChargesHT = meteringChargesH1;
                double lifelineRateDiscountHT = lifelineRateDiscountH1 * purchaceEnergy;
                double mccRfscHT = mccRfscH1 * purchaceEnergy;

                totalChargeVats = generationChargeHT
                        + transmissionSystemChargeHT
                        + systemLossChargeHT
                        + distributionChargeHT
                        + supplySystemChargeHT
                        + meteringChargesHT
                        + lifelineRateDiscountHT
                        + mccRfscHT;






                result.add(bodyLineGenerator("Vat amounts", totalChargeVats) + "\n");
            }
            else{

            }






        }
        result.add(lineBreak(48));
        for (String string : amountDueDetail()) {
            result.add(string);
        }
        return result;
    }

    private String bodyLineGenerator(String description, double rate, double amount) {
        return "" + StringManager.leftJustify(description, 29) + StringManager.rightJustify(this.rateFormat.format(rate), 8) + StringManager.rightJustify(this.amountFormat.format(amount), 11);
    }

    private String bodyLineGeneratorTwo(String description, double rate, double amount) {
        return "" + StringManager.leftJustify(description, 29) + StringManager.rightJustify(this.rateFormat.format(rate), 8) + StringManager.rightJustify(this.rateFormat.format(rate), 8);
    }


    private String bodyLineGenerator(String description, double amount) {
        return "" + StringManager.leftJustify(description, 34) + StringManager.rightJustify(this.totalAmountFormat.format(amount), 14);
    }

    private String footerTotalLineGenerator(String description, double amount) {
        return "" + StringManager.leftJustify(description, 34) + StringManager.rightJustify(this.amountFormat.format(amount), 14);
    }


    private List<String> amountDueDetail() {
        double valueTax = this.compute.getKilowatthour() * 0.0057;

        long diff = Math.round(this.reading.getReading() - this.consumer.getInitialReading());
        double tax2025 = Math.round(diff * 0.0057 * 100.0) / 100.0;

        double tempTotalVat = this.reading.getReading() - this.consumer.getInitialReading();
        int roundedTempTotal = (int) Math.round(tempTotalVat);
        double tempTotal = roundedTempTotal;


        double generationSystemCharge = 5.8599;
        double powerActReduction = -0.1229;
        double transmissionSystemCharge = 1.1597;
        double systemLossCharge = 0.8642;

        double distributionSystemCharge = 0.8449;
        double supplySystemCharge = 0.7732;
        double meteringSystemCharget = 0.4569;
        double meteringRetailCustomer = 5.0000;
        double reinvestmentFundForSusCaPEX = 0.5189;
        double realPropertyTax = 0.0227;
        double businessTaxYr2024_2025 = 0.0057;
        double ucMeNPC_SPUG = 0.1949;
        double ucMeRED = 0.0044;
        double ucSdCharge = 0.0428;
        double fitAllRenewable = 0.1189;
        double localFranchiseTax = 0.0104;

        double generationChargeRate = 0;
        double transmissionChargeRate = 0;
        double demandChargeRate = 0;
        double supplyChargeRate =0;
        double meteringChargeRate = 0;
        double meteringSystemCharge = 0;
        double seniorCitizenCharge = 0;
        double parAdjustment = 0;
        double slGenerationCharge = 0;
        double slTransmissionCharge = 0;

        double totalChargeVats = 0.00;
        double grandTotal=0;
        if ("P".equalsIgnoreCase(this.consumer.getRateCode()) || "R".equalsIgnoreCase(this.consumer.getRateCode())) {
            generationChargeRate = 0.5335 * tempTotal;
            transmissionChargeRate = 0.1338 * tempTotal;
            demandChargeRate = 0.1014 * tempTotal;
            supplyChargeRate = 0.0928 * tempTotal;
            meteringChargeRate = 0.0548 * tempTotal;
            meteringSystemCharge = 0.6 ;
            seniorCitizenCharge = 0.0001 * tempTotal;
            parAdjustment = -0.0147 * tempTotal;
            slGenerationCharge = 0.0671 * tempTotal;
            slTransmissionCharge = 0.0136 * tempTotal;

            double totalGenerationSystemCharge = generationSystemCharge * tempTotal;
            double totalPowerActReduction = powerActReduction * tempTotal;
            double totalTransmissionSystemCharge = transmissionSystemCharge * tempTotal;
            double totalSystemLossCharge = systemLossCharge * tempTotal;

            double totalDistributionSystemCharge = distributionSystemCharge * tempTotal;
            double totalSupplySystemCharge = supplySystemCharge * tempTotal;
            double totalMeteringSystemCharge = meteringSystemCharget * tempTotal;
            double totalMeteringRetailCustomer = meteringRetailCustomer;
            double totalReinvestmentFundForSusCaPEX = reinvestmentFundForSusCaPEX * tempTotal;
            double totalRealPropertyTax = realPropertyTax * tempTotal;
            double totalBusinessTaxYr2024_2025 = businessTaxYr2024_2025 * tempTotal;
            double totalUcMeNPC_SPUG = ucMeNPC_SPUG * tempTotal;
            double totalUcMeRED = ucMeRED * tempTotal;
            double totalUcSdCharge = ucSdCharge * tempTotal;
            double totalFitAllRenewable = fitAllRenewable * tempTotal;
            double totalLocalFranchiseTax = localFranchiseTax * tempTotal;
            System.out.println("Total Generation System Charge: " + totalGenerationSystemCharge);
            System.out.println("Total Power Act Reduction: " + totalPowerActReduction);
            System.out.println("Total Transmission System Charge: " + totalTransmissionSystemCharge);
            System.out.println("Total System Loss Charge: " + totalSystemLossCharge);
            System.out.println("Total Distribution System Charge: " + totalDistributionSystemCharge);
            System.out.println("Total Supply System Charge: " + totalSupplySystemCharge);
            System.out.println("Total Metering System Charge: " + totalMeteringSystemCharge);
            System.out.println("Total Metering Retail Customer: " + totalMeteringRetailCustomer);
            System.out.println("Total Reinvestment Fund for Sus. CaPEX: " + totalReinvestmentFundForSusCaPEX);
            System.out.println("Total Real Property Tax: " + totalRealPropertyTax);
            System.out.println("Total Business Tax Yr. 2024-2025: " + totalBusinessTaxYr2024_2025);
            System.out.println("Total UC-ME (NPC-SPUG): " + totalUcMeNPC_SPUG);
            System.out.println("Total UC-ME (RED): " + totalUcMeRED);
            System.out.println("Total UCSD Charge: " + totalUcSdCharge);
            System.out.println("Total Fit-All (Renewable): " + totalFitAllRenewable);
            System.out.println("Total Local Franchise Tax: " + totalLocalFranchiseTax);
             grandTotal = totalGenerationSystemCharge
                    + totalPowerActReduction
                    + totalTransmissionSystemCharge
                    + totalSystemLossCharge
                    + totalDistributionSystemCharge
                    + totalSupplySystemCharge
                    + totalMeteringSystemCharge
                    + totalMeteringRetailCustomer
                    + totalReinvestmentFundForSusCaPEX
                    + totalRealPropertyTax
                    + totalBusinessTaxYr2024_2025
                    + totalUcMeNPC_SPUG
                    + totalUcMeRED
                    + totalUcSdCharge
                    + totalFitAllRenewable
                    + totalLocalFranchiseTax;

            grandTotal = Math.round(grandTotal * 100.0) / 100.0;
            Log.d("Charge Calculation", "Temp total   vat 1: " + grandTotal);
            totalChargeVats = generationChargeRate + transmissionChargeRate
                    + demandChargeRate + supplyChargeRate + meteringChargeRate
                    + meteringSystemCharge + seniorCitizenCharge + parAdjustment
                    + slGenerationCharge+slTransmissionCharge;


        }
        if ("C".equalsIgnoreCase(this.consumer.getRateCode()) ) {
            double generationChargeRateCommercial = 0;
            double powerActReductionCommercial = 0;
            double transmissionSystemChargeCommercial = 0;
            double systemLossChargeCommercial = 0;
            double distributionSystemChargeCommercial = 0;
            double supplySystemChargeCommercial = 0;
            double meteringSystemChargeCommercial = 0;
            double meteringRetailCustomerCommercial = 0;
            double supplyRetailCommercial = 0;
            double reinvestmentFundForCapexCommercial = 0;
            double realPropertyTaxCommercial = 0;
            double businessTaxCommercial = 0;
            double ucMeNpcSpugCommercial = 0;
            double ucMeRedCommercial = 0;
            double ucsdChargeCommercial = 0;
            double fitAllRenewableCommercial = 0;
            double localFranchiseTaxCommercial = 0;

            generationChargeRateCommercial       = 5.8599 * tempTotal;
            powerActReductionCommercial          = -0.1229 * tempTotal;
            transmissionSystemChargeCommercial   = 1.1597 * tempTotal;
            systemLossChargeCommercial           = 0.8642 * tempTotal;
            distributionSystemChargeCommercial   = 0.8449 * tempTotal;
            supplySystemChargeCommercial         = 0.7732 * tempTotal;
            meteringSystemChargeCommercial       = 0.4569 * tempTotal;
            meteringRetailCustomerCommercial     = 28.72;
            supplyRetailCommercial               = 40.15;
            reinvestmentFundForCapexCommercial   = 0.5189 * tempTotal;
            realPropertyTaxCommercial            = 0.0227 * tempTotal;
            businessTaxCommercial                = 0.0057 * tempTotal;
            ucMeNpcSpugCommercial                = 0.1949 * tempTotal;
            ucMeRedCommercial                    = 0.0044 * tempTotal;
            ucsdChargeCommercial                 = 0.0428 * tempTotal;
            fitAllRenewableCommercial            = 0.1189 * tempTotal;
            localFranchiseTaxCommercial          = 0.0104 * tempTotal;
            grandTotal=
                    generationChargeRateCommercial +
                            powerActReductionCommercial +
                            transmissionSystemChargeCommercial +
                            systemLossChargeCommercial +
                            distributionSystemChargeCommercial +
                            supplySystemChargeCommercial +
                            meteringSystemChargeCommercial +
                            meteringRetailCustomerCommercial +
                            supplyRetailCommercial +
                            reinvestmentFundForCapexCommercial +
                            realPropertyTaxCommercial +
                            businessTaxCommercial +
                            ucMeNpcSpugCommercial +
                            ucMeRedCommercial +
                            ucsdChargeCommercial +
                            fitAllRenewableCommercial +
                            localFranchiseTaxCommercial;

            double generationChargeRateCommercialVat = 0;
            double transmissionSystemChargeCommercialVat = 0;
            double demandChargePerKwCommercialVat = 0;
            double supplySystemCharge2CommercialVat = 0;
            double meteringChargesCommercialVat = 0;
            double meteringSystemChargeCommercialVat = 0;
            double seniorCitizenSubsidyChargeCommercialVat = 0;
            double powerActReductionAdjCommercialVat = 0;
            double slGenerationCommercialVat = 0;
            double slTransmissionCommercialVat = 0;

// Assign values
            generationChargeRateCommercialVat         = 0.5335 * tempTotal;
            transmissionSystemChargeCommercialVat     = 0.1338 * tempTotal;
            demandChargePerKwCommercialVat             = 0.1014 * tempTotal;
            supplySystemCharge2CommercialVat           = 0.0928 * tempTotal;
            meteringChargesCommercialVat               = 0.0548 * tempTotal;
            meteringSystemChargeCommercialVat         = 8.2644 ;
            seniorCitizenSubsidyChargeCommercialVat    = 0.0001 * tempTotal;
            powerActReductionAdjCommercialVat          = -0.0147 * tempTotal;
            slGenerationCommercialVat                  = 0.0671 * tempTotal;
            slTransmissionCommercialVat                = 0.0136 * tempTotal;


 totalChargeVats =
                    generationChargeRateCommercialVat +
                            transmissionSystemChargeCommercialVat +
                            demandChargePerKwCommercialVat +
                            supplySystemCharge2CommercialVat +
                            meteringChargesCommercialVat +
                            meteringSystemChargeCommercialVat +
                            seniorCitizenSubsidyChargeCommercialVat +
                            powerActReductionAdjCommercialVat +
                            slGenerationCommercialVat +
                            slTransmissionCommercialVat;
        }
        if ("H".equalsIgnoreCase(this.consumer.getRateCode()) ) {
            double generationChargeH1 = 0.6174;
            double transmissionSystemChargeH1 = 0.031;
            double systemLossChargeH1 = 0.0914;
            double distributionChargeH1 = 32.15;
            double supplySystemChargeH1 = 4.818;
            double meteringChargesH1 = 3.4466;
            double lifelineRateDiscountH1 = 0.0084;
            double mccRfscH1 = 0.0623;

            double purchaceEnergy =tempTotal*420.00;
            double demand =420*0.889;

            double generationChargeHT = generationChargeH1 * purchaceEnergy;
            double transmissionSystemChargeHT = transmissionSystemChargeH1 * demand;
            double systemLossChargeHT = systemLossChargeH1 * purchaceEnergy;
            double distributionChargeHT = distributionChargeH1 * demand;
            double supplySystemChargeHT = supplySystemChargeH1;
            double meteringChargesHT = meteringChargesH1;
            double lifelineRateDiscountHT = lifelineRateDiscountH1 * purchaceEnergy;
            double mccRfscHT = mccRfscH1 * purchaceEnergy;

            totalChargeVats = generationChargeHT
                    + transmissionSystemChargeHT
                    + systemLossChargeHT
                    + distributionChargeHT
                    + supplySystemChargeHT
                    + meteringChargesHT
                    + lifelineRateDiscountHT
                    + mccRfscHT;

            // --- Second group of charges ---
            double generationSystemChargeH1 = 7.7171;
            double demandChargePerKwhH1 = 40.85;
            double systemLossCharge2H1 = 1.1812;
            double distributionChargesH1 = 267.90;
            double supplySystemCharge2H1 = 40.1500;
            double meteringCharge2H1 = 28.7200;
            double lifelineRateDiscount2H1 = 0.0918;
            double missionaryElectrificationH1 = 0.1977;
            double meChargeForRenewableEnergyH1 = 0.0017;
            double strandedDebtChargeH1 = 0.0428;
            double rptPreviousYearsH1 = 0.1055;
            double rptCurrentH1 = 0.025;
            double localFranchiseTaxH1 = 0.0096;
            double seniorCitizenSubChargeH1 = 0.0015;
            double mccRfsc2H1 = 0.5189;

            double generationSystemChargeHT = generationSystemChargeH1 * purchaceEnergy;
            double demandChargePerKwhHT = demandChargePerKwhH1* demand;
            double systemLossChargeHT2 = systemLossCharge2H1 * purchaceEnergy;
            double distributionChargesHT = distributionChargesH1* demand;
            double supplySystemChargeHT2 = supplySystemCharge2H1;
            double meteringChargeHT2 = meteringCharge2H1 ;
            double lifelineRateDiscountHT2 = lifelineRateDiscount2H1 * purchaceEnergy;
            double missionaryElectrificationHT = missionaryElectrificationH1 * purchaceEnergy;
            double meChargeForRenewableEnergyHT = meChargeForRenewableEnergyH1* purchaceEnergy;
            double strandedDebtChargeHT = strandedDebtChargeH1 * purchaceEnergy;
            double rptPreviousYearsHT = rptPreviousYearsH1 * purchaceEnergy;
            double rptCurrentHT = rptCurrentH1* purchaceEnergy;
            double localFranchiseTaxH2 = localFranchiseTaxH1 * purchaceEnergy;
            double seniorCitizenSubChargeHT = seniorCitizenSubChargeH1* purchaceEnergy;
            double mccRfscHT2 = mccRfsc2H1 * purchaceEnergy;

            Log.d("BillingDebug", "generationSystemChargeHT: " + generationSystemChargeHT);
            Log.d("BillingDebug", "demandChargePerKwhHT: " + demandChargePerKwhHT);
            Log.d("BillingDebug", "systemLossChargeHT2: " + systemLossChargeHT2);
            Log.d("BillingDebug", "distributionChargesHT: " + distributionChargesHT);
            Log.d("BillingDebug", "supplySystemChargeHT2: " + supplySystemChargeHT2);
            Log.d("BillingDebug", "meteringChargeHT2: " + meteringChargeHT2);
            Log.d("BillingDebug", "lifelineRateDiscountHT2: " + lifelineRateDiscountHT2);
            Log.d("BillingDebug", "missionaryElectrificationHT: " + missionaryElectrificationHT);
            Log.d("BillingDebug", "meChargeForRenewableEnergyHT: " + meChargeForRenewableEnergyHT);
            Log.d("BillingDebug", "strandedDebtChargeHT: " + strandedDebtChargeHT);
            Log.d("BillingDebug", "rptPreviousYearsHT: " + rptPreviousYearsHT);
            Log.d("BillingDebug", "rptCurrentHT: " + rptCurrentHT);
            Log.d("BillingDebug", "localFranchiseTaxH2: " + localFranchiseTaxH2);

            Log.d("BillingDebug", "seniorCitizenSubChargeHT: " + seniorCitizenSubChargeHT);
            Log.d("BillingDebug", "mccRfscHT2: " + mccRfscHT2);

            grandTotal = generationSystemChargeHT
                    + demandChargePerKwhHT
                    + systemLossChargeHT2
                    + distributionChargesHT
                    + supplySystemChargeHT2
                    + meteringChargeHT2
                    + lifelineRateDiscountHT2
                    + missionaryElectrificationHT
                    + meChargeForRenewableEnergyHT
                    + strandedDebtChargeHT
                    + rptPreviousYearsHT
                    + rptCurrentHT
                    + localFranchiseTaxH2
                    + seniorCitizenSubChargeHT
                    + mccRfscHT2;

            Log.d("BillingDebug", "grandTotal: " + grandTotal);


            // --- Grand Total ---
          //  grandTotal= totalChargeHT1 + totalChargeHT2;
        }
        else{

        }


        double totalWithTax = this.compute.amountAfterDue().doubleValue() + tax2025;
        List<String> result = new ArrayList();
        result.add(PrinterControls.emphasized(true));
        result.add(footerTotalLineGenerator(
                "TOTAL AMT DUE ON OR BEFORE DUE DATE",
                grandTotal + totalChargeVats
        ) + "\n");
//        result.add(footerTotalLineGenerator(
//                "TOTAL AMT DUE ON OR BEFORE DUE DATE",
//                this.compute.totalCharge() + this.compute.totalVat() + tax2025
//        ) + "\n"); // this.compute.FTresult() + this.compute.RptPrevTax()) + "\n");

        double finalTotal = grandTotal + totalChargeVats;
        double finalTempTotal = (grandTotal + totalChargeVats) * 0.03 * 1.12;
        double grandSum = 0.0;
        double serviceFeeTemp= 0.0;
        double surchargeTemp= 0.0;
        double demandTemp=0.889;
        double multiplier=420.0;
        double finalDemad=multiplier*demandTemp;//373.38
        double distriTemp=267.9*finalDemad;

        double distriCharge= distriTemp* 0.04 * 1.12;
        if ("H".equalsIgnoreCase(this.consumer.getRateCode())) {
            serviceFeeTemp = distriCharge;
            surchargeTemp = finalTotal * 0.03 * 1.12;
            grandSum = surchargeTemp + serviceFeeTemp + finalTotal;

            Log.d("BillingDebug", "Rate Code: " + this.consumer.getRateCode());
            Log.d("BillingDebug", "distriCharge: " + distriCharge);
            Log.d("BillingDebug", "finalTotal: " + finalTotal);
            Log.d("BillingDebug", "serviceFeeTemp: " + serviceFeeTemp);
            Log.d("BillingDebug", "surchargeTemp: " + surchargeTemp);
            Log.d("BillingDebug", "grandSum: " + grandSum);
        }
        else{
            surchargeTemp=finalTempTotal;
            serviceFeeTemp=112.00;
            grandSum = finalTotal + finalTempTotal + 112.00;
        }

        result.add(footerTotalLineGenerator("SERVICE FEE AND", serviceFeeTemp));
        result.add(footerTotalLineGenerator("SURCHARGE AFTER DUE(" + surchargeTemp + ")", surchargeTemp) + "\n");
        result.add(footerTotalLineGenerator("ADD: VAT", this.compute.serviceFeeVat() + this.compute.surchargeVat()));
        result.add(lineBreak(48));

        result.add(footerTotalLineGenerator("TOTAL AMOUNT AFTER DUE DATE", grandSum) + "\n");
        result.add(StringManager.rightJustify("==============", 48) + "\n");
        if (this.consumer.getArrears() != 0.0d) {
            result.add("ARREARS " + String.valueOf(this.consumer.getNumberOfArrears()) + "(surcharge & service fee " + "inclusive)\n");
            result.add(footerTotalLineGenerator("as of " + this.userProfile.getInitialReadingDate(), this.consumer.getArrears()) + "\n");
        }
        result.add("\n");
        //TEMPORARY HIDE VATABLE AMOUNT
        //result.add(StringManager.leftJustify(StringManager.leftJustify("VATABLE AMOUNT", 34) + StringManager.rightJustify(this.amountFormat.format(this.compute.taxableEnergy()), 14), 48) + "\n");
        //result.add("\n");
        return result;
    }

    protected List<String> soaFooter() {
        Time myTime = new Time();
        myTime.set(this.reading.getTransactionDate());
        List<String> result = new ArrayList();
        result.add(StringManager.leftJustify("Date Delivered", 38) + StringManager.rightJustify(myTime.format("%D"), 10) + "\n");
        result.add(StringManager.leftJustify("Disconnection Date", 38) + StringManager.rightJustify(this.userProfile.getDiscoDate(), 10) + "\n");
        result.add(StringManager.leftJustify("SOA Control Number: ", 20) + StringManager.rightJustify(this.reading.getSoaPrefix() + "-" + StringManager.rightJustify(String.valueOf(this.reading.getSoaNumber()), 4).replace(" ", "0"), 28) + "\n");
        result.add("Meter Reader: " + this.userProfile.getName() + "\n");
        result.add(StringManager.centerJustify("THANK YOU FOR PAYING YOUR ELECTRIC BILL ON TIME", 48) + "\n");
        result.add(StringManager.centerJustify("GOD BLESS YOU.", 48) + "\n");
        //result.add(String.valueOf('\n'));
        result.add(lineBreak(48));
        result.add(StringManager.centerJustify("Persuant  to  Republic  Act  No.7832,  otherwise", 48) + "\n");
        result.add(StringManager.centerJustify("known  as  the Anti-Pilferage of Electricity and", 48) + "\n");
        result.add(StringManager.centerJustify("Theft  of  Electic  Transmission Lines/Materials", 48) + "\n");
        result.add(StringManager.centerJustify("Act of 1994, STEALING OF ELECTRICITY IS A CRIME.", 48) + "\n");
        result.add("\n");
        result.add(PrinterControls.emphasized(true));
        result.add(StringManager.centerJustify("PAGPAKABANA BANTAY KURYENTE NAAY GANTI,ipahibalo", 48) + "\n");
        result.add(StringManager.centerJustify("sa pinakaduol nga buhatan sa LANECO\035", 48) + "\n");
        result.add(PrinterControls.emphasized(false));
        //result.add(StringManager.pageBreak());
        result.add(pageBreak());
        return result;
    }

    private String pageBreak() {
        String str = "";
        for (int i = 0; i <= 2; i++) {
            str = str + "\n";
        }
        return str;
    }

    public static String lineBreak(int numChar) {
        return "" + StringManager.leftJustify("", numChar).replace(" ", "-") + "\n";
    }
}
