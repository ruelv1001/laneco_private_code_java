package com.laneco.readandbill;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.generic.readandbill.R;
import com.androidapp.mytools.objectmanager.ProgressDialogMaker;
import com.androidapp.mytools.objectmanager.StringManager;
import com.generic.readandbill.database.FieldFindingDataSource;
import com.lamerman.FileDialog;
import com.laneco.readandbill.database.Consumer;
import com.laneco.readandbill.database.ConsumerDataSource;
import com.laneco.readandbill.database.RateDataSource;
import com.laneco.readandbill.database.Rates;
import com.laneco.readandbill.database.Reading;
import com.laneco.readandbill.database.ReadingDataSource;
import com.laneco.readandbill.database.UserProfile;
import com.laneco.readandbill.database.UserProfileDataSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashScreen extends com.generic.readandbill.SplashScreen {
    private static final int REQUEST_LOAD = 10;
    private static final int REQUEST_SAVE = 20;
    private static final int REQUEST_STORAGE_PERMISSION = 30;
    public static UserProfile up = null;
    public static final String version = "2.2.0.1";
    protected ProgressDialog barProgressDialog;
    private ConsumerDataSource dsConsumer;
    private RateDataSource dsRates;
    private ReadingDataSource dsReading;
    private UserProfileDataSource dsUserProfile;
    private FieldFindingDataSource dsFF;

    // Path to the infiles directory
    private static final String INFILES_FOLDER = "infiles";
    private static final int REQUEST_BLUETOOTH_PERMISSION = 40;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dsConsumer = new ConsumerDataSource(this);
        this.dsReading = new ReadingDataSource(this);
        this.dsRates = new RateDataSource(this);
        this.dsUserProfile = new UserProfileDataSource(this);
        up = new UserProfile();
        up = this.dsUserProfile.getUserProfile();
        setTitle("LANECO Read And Bill 1.0.3.9");
        this.splashScreenImage = (ImageView) findViewById(R.id.imageView1);
        this.splashScreenImage.setImageResource(R.drawable.lanecologo);

        // Check for storage permissions on app start
        checkStoragePermissions();

        // Create infiles directory if it doesn't exist
        createInfilesDirectory();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN
                        },
                        REQUEST_BLUETOOTH_PERMISSION
                );
            }
        }
    }

    // Create infiles directory in internal storage
    private void createInfilesDirectory() {
        File infilesDir = new File(getInfilesPath());
        if (!infilesDir.exists()) {
            boolean created = infilesDir.mkdirs();
            if (created) {
                Log.d("SplashScreen", "infiles directory created successfully");
            } else {
                Log.e("SplashScreen", "Failed to create infiles directory");
            }
        }
    }

    // Get path to infiles directory
    private String getInfilesPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+, use app-specific directory
            return Environment.getExternalStorageDirectory() + File.separator + "ReadAndBill" + File.separator + INFILES_FOLDER;
        } else {
            // For older Android versions
            return Environment.getExternalStorageDirectory() + File.separator + "ReadAndBill" + File.separator + INFILES_FOLDER;
        }
    }

    // Check and request storage permissions
    private void checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_STORAGE_PERMISSION);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, REQUEST_STORAGE_PERMISSION);
                }
            } else {
                // Permission already granted, create directory
                createInfilesDirectory();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android 6 to Android 10
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_STORAGE_PERMISSION
                );
            } else {
                // Permission already granted, create directory
                createInfilesDirectory();
            }
        } else {
            // No runtime permission needed for Android 5 and below
            createInfilesDirectory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
                createInfilesDirectory();
            } else {
                Toast.makeText(this, "Storage permission required for file operations", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                Toast.makeText(this, "Bluetooth permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth permission required for scanning or connecting", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission granted for Android 11+
                    Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
                    createInfilesDirectory();
                } else {
                    // Permission denied for Android 11+
                    Toast.makeText(this, "Storage permission required for file operations", Toast.LENGTH_LONG).show();
                }
            }
            return;
        }

        if (resultCode != -1) {
            return;
        }
        if (requestCode == REQUEST_LOAD) {
            retrieveData(data.getStringExtra(FileDialog.RESULT_PATH));
        } else if (requestCode == REQUEST_SAVE) {
            UserProfile up = this.dsUserProfile.getUserProfile();
            Time gadgetTime = new Time();
            gadgetTime.setToNow();
            generateResultFile(initializeResultFields(), data.getStringExtra(FileDialog.RESULT_PATH) + "/" + up.getName().replace(",", "") + " " + up.getRoute() + " " + gadgetTime.format("%D-%R").replace("/", "").replace(":", "") + ".prn");
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.iConsumerList) {
            if (this.dsConsumer.existing()) {
                startActivity(new Intent(this, MyConsumerList.class));
                return true;
            }
            new Builder(this).setTitle("Database not found!").setMessage("Please process the text file before tapping consumer list").setPositiveButton("Done", null).create().show();
            return true;
        } else if (item.getItemId() == R.id.iSummaryList) {
            startActivity(new Intent(this, SummaryList.class));
            return true;
        } else if (item.getItemId() == R.id.iProcessTextFile) {
            if (checkStorageAccess()) {
                Intent intent = new Intent(getBaseContext(), FileDialog.class);
                intent.putExtra(FileDialog.START_PATH, getInfilesPath());
                intent.putExtra(FileDialog.SELECTION_MODE, 1);
                intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
                intent.putExtra(FileDialog.FORMAT_FILTER, new String[]{"txt"});
                startActivityForResult(intent, REQUEST_LOAD);
            }
            return true;
        } else if (item.getItemId() != R.id.iGenerateResult) {
            return super.onOptionsItemSelected(item);
        } else {
            if (checkStorageAccess()) {
                Intent intent = new Intent(getBaseContext(), FileDialog.class);
                intent.putExtra(FileDialog.START_PATH, getInfilesPath());
                intent.putExtra(FileDialog.SELECTION_MODE, 1);
                intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
                intent.putExtra(FileDialog.FORMAT_FILTER, new String[]{"prn"});
                startActivityForResult(intent, REQUEST_SAVE);
            }
            return true;
        }
    }

    // Check if storage access is available
    private boolean checkStorageAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                checkStoragePermissions();
                return false;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                checkStoragePermissions();
                return false;
            }
        }
        return true;
    }

    // Helper method to safely get field values
    private String getField(String[] data, int index, String defaultValue) {
        if (index >= 0 && index < data.length) {
            String value = data[index].trim();
            return value.isEmpty() ? defaultValue : value;
        }
        return defaultValue;
    }

    // Helper method to safely parse double values
    private double parseDoubleSafe(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            Log.e("ParseError", "Invalid double value: " + value);
            return defaultValue;
        }
    }

    // Helper method to safely parse integer values
    private int parseIntSafe(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            Log.e("ParseError", "Invalid integer value: " + value);
            return defaultValue;
        }
    }

    // Helper method to safely parse boolean values
    private boolean parseBooleanSafe(String value, boolean defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        String trimmed = value.trim().toUpperCase();
        if (trimmed.equals("Y") || trimmed.equals("T") || trimmed.equals("TRUE") || trimmed.equals("1")) {
            return true;
        } else if (trimmed.equals("N") || trimmed.equals("F") || trimmed.equals("FALSE") || trimmed.equals("0")) {
            return false;
        }
        return defaultValue;
    }

    // Updated method for retrieving data from file
    protected boolean retrieveData(String path) {
        if (new File(path).exists()) {
            initializeDatabase();
            processRawData(retrieveStringFromFile(path));
        } else {
            AlertDialog ad = new Builder(this).create();
            ad.setTitle("File not found!");
            ad.show();
        }
        return false;
    }

    // Updated method to handle file operations
    public List<String> retrieveStringFromFile(String path) {
        List<String> result = new ArrayList<>();
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SplashScreen.this, "Error reading file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        return result;
    }

    protected boolean generateResultFile(List<String> result, String path) {
        if (result.size() != 0) {
            this.barProgressDialog = ProgressDialogMaker.myProgressBar(this, "Generating " + this.dsUserProfile.getUserProfile().getRoute(), "Generating text file please wait..", result.size());
            this.barProgressDialog.show();
            new C00402(path, result).start();
            return true;
        }
        nothingDialog().show();
        return false;
    }

    class C00402 extends Thread {
        final String val$path;
        final List val$result;

        C00402(String str, List list) {
            this.val$path = str;
            this.val$result = list;
        }

        public void run() {
            try {
                // Ensure parent directory exists
                File outputFile = new File(this.val$path);
                File parentDir = outputFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }

                FileWriter fw = new FileWriter(outputFile);
                for (int i = 0; i <= this.val$result.size() - 1; i++) {
                    fw.append((CharSequence) this.val$result.get(i));
                    if (i % 4 == 0) {
                        Thread.sleep(500);
                    }
                    ProgressDialogMaker.progressHandler.post(ProgressDialogMaker.increaseProgress(SplashScreen.this.barProgressDialog));
                }
                fw.flush();
                fw.close();
                SplashScreen.this.barProgressDialog.dismiss();
                SplashScreen.this.runOnUiThread(new C00391());
            } catch (IOException e) {
                e.printStackTrace();
                SplashScreen.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SplashScreen.this, "Error writing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }

    // The main parsing method with fixes for field count issues
    private void processRawData(List<String> rawData) {
        this.barProgressDialog = ProgressDialogMaker.myProgressBar(this, "Processing " + this.dsUserProfile.getUserProfile().getRoute(), "Processing text file please wait..", rawData.size());
        this.barProgressDialog.show();
        new Thread(new C00381(rawData)).start();
    }

    private Consumer listToConsumer(String rawData) {
        // Trim the raw data first to remove any leading/trailing whitespace
        rawData = rawData.trim();

        String[] data = StringManager.listTrimmer(rawData.split("~"));

        // Log data count for debugging
        Log.d("Parser", "Total fields: " + data.length);

        // Expected field count is 103 (0-102)
        if (data.length != 107) {
            Log.w("Parser", "Unexpected field count: " + data.length + " (expected 103)");
            Log.w("Parser", "Record start: " + rawData.substring(0, Math.min(50, rawData.length())));

            // For MERLYN ALVAREZ specifically, log all fields
            if (rawData.contains("MERLYN ALVAREZ")) {
                Log.w("Parser", "=== MERLYN ALVAREZ DEBUG ===");
                Log.w("Parser", "Full record: " + rawData);
                for (int i = 0; i < data.length; i++) {
                    Log.w("Parser", "Field[" + i + "] = [" + data[i] + "]");
                }
                Log.w("Parser", "Last field (index " + (data.length - 1) + ") = [" + data[data.length - 1] + "]");
            }

            // Handle missing fields by padding the array
            if (data.length < 107) {
                String[] paddedData = new String[107];
                System.arraycopy(data, 0, paddedData, 0, data.length);
                for (int i = data.length; i < 107; i++) {
                    paddedData[i] = "";
                }
                data = paddedData;
                Log.w("Parser", "Padded data to 103 fields");
            } else if (data.length > 107) {
                // Truncate if there are too many fields
                data = Arrays.copyOf(data, 107);
                Log.w("Parser", "Truncated data to 103 fields");
            }
        }

        Consumer consumer = new Consumer();
        consumer.setAccountNumber(getField(data, 0, ""));
        consumer.setConnCode(getField(data, 1, ""));
        consumer.setInitialReading(parseDoubleSafe(getField(data, 2, "0"), 0.0));
        consumer.setMeterSerial(getField(data, 3, ""));
        consumer.setRateCode(getField(data, 4, ""));
        consumer.setName(getField(data, 5, ""));
        consumer.setAddress(getField(data, 6, ""));
        consumer.setContracted(parseBooleanSafe(getField(data, 7, "N"), false));
        consumer.setMultiplier(parseDoubleSafe(getField(data, 8, "1.0"), 1.0));
        consumer.setCoreLoss(parseDoubleSafe(getField(data, 43, "0"), 0.0));
        consumer.setTransformerLostTestResult(parseDoubleSafe(getField(data, 44, "0"), 0.0));
        consumer.setDemandMultiplier(parseDoubleSafe(getField(data, 49, "0"), 0.0));
        consumer.setDemandMin(parseDoubleSafe(getField(data, 50, "0"), 0.0));
        consumer.setDemandMax(parseDoubleSafe(getField(data, 51, "0"), 0.0));
        consumer.setArMats(parseDoubleSafe(getField(data, 56, "0"), 0.0));
        consumer.setScap(parseDoubleSafe(getField(data, 57, "0"), 0.0));
        consumer.setRefund(parseDoubleSafe(getField(data, 58, "0"), 0.0));
        consumer.setHelp(parseDoubleSafe(getField(data, 59, "0"), 0.0));
        consumer.setPilfer(parseDoubleSafe(getField(data, 60, "0"), 0.0));
        consumer.setSCSwitch(parseBooleanSafe(getField(data, 61, "F"), false));
        consumer.setNumberOfArrears(parseIntSafe(getField(data, 62, "0"), 0));
        consumer.setArrears(parseDoubleSafe(getField(data, 63, "0"), 0.0));
        consumer.setAveKwh(parseDoubleSafe(getField(data, 80, "0"), 0.0));
        consumer.setDateEnergized(getField(data, 81, ""));
        consumer.setMeterBrand(getField(data, 82, ""));
        consumer.setTransformerNumber(getField(data, 83, ""));
        consumer.setKw(parseDoubleSafe(getField(data, 84, "0"), 0.0));
        consumer.setTransformerRental(parseDoubleSafe(getField(data, 86, "0"), 0.0));
        consumer.setDemandCharge(parseDoubleSafe(getField(data, 85, "0"), 0.0));
        consumer.setDisco(parseDoubleSafe(getField(data, 87, "0"), 0.0));
        consumer.setIncentives(parseDoubleSafe(getField(data, 88, "0"), 0.0));
        consumer.setMaterial(parseDoubleSafe(getField(data, 89, "0"), 0.0));
        consumer.setEquiptment(parseDoubleSafe(getField(data, 90, "0"), 0.0));
        consumer.setOthersSurcharge(parseDoubleSafe(getField(data, 91, "0"), 0.0));
        consumer.setdaaRefund(parseDoubleSafe(getField(data, 97, "0"), 0.0));
        consumer.setlocalFranchiseTax(parseDoubleSafe(getField(data, 98, "0"), 0.0));
        consumer.setrptprevTax(parseDoubleSafe(getField(data, 99, "0"), 0.0));
        consumer.setIsLifeLine(getField(data, 101, ""));

        Rates rate = this.dsRates.getConsumerRate(consumer.getRateCode());
        if (rate.getId() == -1) {
            rate.setScSwitch(Boolean.valueOf(consumer.isScSwitch()));
            rate.setRateCode(consumer.getRateCode());
            rate.setGenSys(parseDoubleSafe(getField(data, 9, "0"), 0.0));
            rate.setHostComm(parseDoubleSafe(getField(data, 10, "0"), 0.0));
            rate.setIcera(parseDoubleSafe(getField(data, 11, "0"), 0.0));
            rate.setTcDemand(parseDoubleSafe(getField(data, 12, "0"), 0.0));
            rate.setTcSystem(parseDoubleSafe(getField(data, 13, "0"), 0.0));
            rate.setSystemLoss(parseDoubleSafe(getField(data, 14, "0"), 0.0));
            rate.setDcDemand(parseDoubleSafe(getField(data, 15, "0"), 0.0));
            rate.setDcDistribution(parseDoubleSafe(getField(data, 16, "0"), 0.0));
            rate.setScSupplySys(parseDoubleSafe(getField(data, 17, "0"), 0.0));
            rate.setScRetailCust(parseDoubleSafe(getField(data, 18, "0"), 0.0));
            rate.setMcSys(parseDoubleSafe(getField(data, 19, "0"), 0.0));
            rate.setMcRetailCust(parseDoubleSafe(getField(data, 20, "0"), 0.0));
            rate.setUcsd(parseDoubleSafe(getField(data, 23, "0"), 0.0));
            rate.setUcme(parseDoubleSafe(getField(data, 24, "0"), 0.0));
            rate.setUcStrandedContractCost(parseDoubleSafe(getField(data, 25, "0"), 0.0));
            rate.setUcec(parseDoubleSafe(getField(data, 26, "0"), 0.0));
            rate.setFeedTariffAllowance(parseDoubleSafe(getField(data, 27, "0"), 0.0));
            rate.setParr(parseDoubleSafe(getField(data, 29, "0"), 0.0));
            rate.setLifeLineSubsidy(parseDoubleSafe(getField(data, 30, "0"), 0.0));
            rate.setSeniorCitizenDiscount(parseDoubleSafe(getField(data, 32, "0"), 0.0));
            rate.setSeniorCitizenSubsidy(parseDoubleSafe(getField(data, 33, "0"), 0.0));
            rate.setfranchiseTax(parseDoubleSafe(getField(data, 35, "0"), 0.0));
            rate.setPrevYearAdjPowerCost(parseDoubleSafe(getField(data, 36, "0"), 0.0));
            rate.setReinvestmentFundSustCapex(parseDoubleSafe(getField(data, 37, "0"), 0.0));
            rate.setVatGensys(parseDoubleSafe(getField(data, 64, "0"), 0.0));
            rate.setVatPARR(parseDoubleSafe(getField(data, 65, "0"), 0.0));
            rate.setVatIcera(parseDoubleSafe(getField(data, 66, "0"), 0.0));
            rate.setVatTcSystem(parseDoubleSafe(getField(data, 67, "0"), 0.0));
            rate.setVatTcDemand(parseDoubleSafe(getField(data, 68, "0"), 0.0));
            rate.setVatDcDistribution(parseDoubleSafe(getField(data, 69, "0"), 0.0));
            rate.setVatDcDemand(parseDoubleSafe(getField(data, 70, "0"), 0.0));
            rate.setVatScSupply(parseDoubleSafe(getField(data, 71, "0"), 0.0));
            rate.setVatMcSystem(parseDoubleSafe(getField(data, 72, "0"), 0.0));
            rate.setVatLifelineSubsidy(parseDoubleSafe(getField(data, 73, "0"), 0.0));
            rate.setVatReinvestmentFundSustCapex(parseDoubleSafe(getField(data, 74, "0"), 0.0));
            rate.setVatSeniorCitizen(parseDoubleSafe(getField(data, 75, "0"), 0.0));
            rate.setVatScRetail(parseDoubleSafe(getField(data, 76, "0"), 0.0));
            rate.setVatMcRetail(parseDoubleSafe(getField(data, 77, "0"), 0.0));
            rate.setVatSystemLoss(parseDoubleSafe(getField(data, 78, "0"), 0.0));
            rate.setVatSystemLossTransmission(parseDoubleSafe(getField(data, 79, "0"), 0.0));
            rate.setUcmeRed(parseDoubleSafe(getField(data, 92, "0"), 0.0));
            rate.setRealPropertyTax(parseDoubleSafe(getField(data, 96, "0"), 0.0));
            rate.setTransmissionSystemCharge(parseDoubleSafe(getField(data, 68, "0"), 0.0));
            String line100 = data[100];


            String numericPart = line100.replaceAll("[^0-9.+-Ee]", "");

            rate.setBusinessTax(Double.parseDouble(numericPart));
            rate.setIsLifeLine(data[101]);
            // Get the IsLifeLine value at index 101
            String islifeline = getField(data, 101, "N");

            rate.setIsLifeLine(islifeline);

            // Get the last value (0.0371) at index 102
            double geaAllValue = parseDoubleSafe(getField(data, 102, "0"), 0.0);
            rate.setGeaAll(geaAllValue);
            double setTempRec = parseDoubleSafe(getField(data, 103, ".0045"), 0.0);
            rate.setRec(setTempRec);
            double setTempRegulated = parseDoubleSafe(getField(data, 104, "0"), 0.0);
            rate.setRegulatedNGCPCharge(setTempRegulated);
            double AncillaryServiceCharge = parseDoubleSafe(getField(data, 105, "0"), 0.0);
            rate.setAncillaryServiceCharge(AncillaryServiceCharge);

            double AncillaryTransmissionCharge = parseDoubleSafe(getField(data, 106, "0"), 0.0);
            rate.setancillaryTransmissionDemandCharge(AncillaryTransmissionCharge);
            Log.d("last nak1o", String.valueOf(AncillaryTransmissionCharge));
            // Log the last value
            Log.d("last nako2",String.valueOf(AncillaryServiceCharge));

            this.dsRates.createRates(rate);
        }
        return consumer;
    }

    protected UserProfile listToUserProfile(String dataValues) {
        String[] data = StringManager.listTrimmer(dataValues.split("~"));
        UserProfile up = new UserProfile();
        up.setRoute(getField(data, 0, ""));
        up.setReadingDate(getField(data, 1, ""));
        up.setInitialReadingDate(getField(data, 2, ""));
        up.setName(getField(data, 3, ""));
        return up;
    }

    protected void initializeDatabase() {
        this.dsConsumer.truncate();
        this.dsRates.truncate();
        if (!this.dsReading.tableExist()) {
            this.dsReading.truncate();
        }
        this.dsUserProfile.truncate();
    }

    protected List<String> initializeResultFields() {
        DecimalFormat amtFormater = new DecimalFormat("#####0.0");
        List<String> result = new ArrayList();
        if (this.dsFF == null) {
            this.dsFF = new FieldFindingDataSource();
        }
        if (this.dsReading.getResultReading().size() > 0) {
            for (Reading reading : this.dsReading.getResultReading()) {
                result.add(StringManager.leftJustify(this.dsConsumer.getConsumer(Long.valueOf(reading.getIdConsumer())).getOrigAccountNumber(), 13) + " " + StringManager.leftJustify(amtFormater.format(reading.getReading()), REQUEST_LOAD) + " " + StringManager.leftJustify(amtFormater.format(reading.getDemand()), REQUEST_LOAD) + " " + StringManager.leftJustify(this.dsFF.getDescription(this.dsFF.getCode(reading.getFieldFinding())) + "-" + reading.getRemarks(), 51) + '\r' + "" + '\n');
            }
            result.add("TM Start\r\n");
            for (com.generic.readandbill.database.Reading reading2 : this.dsReading.getAllReadings()) {
                Time readTime = new Time();
                readTime.set(reading2.getTransactionDate());
                result.add(readTime.format("%D %r") + " " + this.dsConsumer.getConsumer(Long.valueOf(reading2.getIdConsumer())).getAccountNumber() + " " + StringManager.rightJustify(amtFormater.format(reading2.getKilowatthour()), REQUEST_LOAD) + " " + StringManager.rightJustify(amtFormater.format(reading2.getDemand()), REQUEST_LOAD) + '\r' + "" + '\n');
            }
        }
        return result;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        MenuItem newConsumer = menu.findItem(R.id.iNewConList);
        MenuItem zoneReport = menu.findItem(R.id.iZoneReport);
        newConsumer.setVisible(false);
        zoneReport.setChecked(false);
        return true;
    }

    class C00381 implements Runnable {
        final List val$rawData;

        C00381(List list) {
            this.val$rawData = list;
        }

        public void run() {
            for (int i = 0; i <= this.val$rawData.size() - 1; i++) {
                if (i == 0) {
                    SplashScreen.this.dsUserProfile.createUserProfile(SplashScreen.this.listToUserProfile((String) this.val$rawData.get(i)));
                } else {
                    SplashScreen.this.dsConsumer.createConsumer(SplashScreen.this.listToConsumer((String) this.val$rawData.get(i)));
                }
                ProgressDialogMaker.progressHandler.post(ProgressDialogMaker.increaseProgress(SplashScreen.this.barProgressDialog));
            }
            SplashScreen.this.barProgressDialog.dismiss();
            SplashScreen.this.runOnUiThread(new C00371());
        }
    }

    class C00371 implements Runnable {
        C00371() {
        }

        public void run() {
            new Builder(SplashScreen.this).setTitle("Done").setMessage("Processing Text File Complete!").setPositiveButton("Done", null).create().show();
            SplashScreen.this.setRequestedOrientation(1);
        }
    }

    class C00391 implements Runnable {
        C00391() {
        }

        public void run() {
            SplashScreen.this.doneDialog("Result").show();
            SplashScreen.this.setRequestedOrientation(1);
        }
    }
}