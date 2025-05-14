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

	// The rest of your existing methods remain unchanged
	private void processRawData(List<String> rawData) {
		this.barProgressDialog = ProgressDialogMaker.myProgressBar(this, "Processing " + this.dsUserProfile.getUserProfile().getRoute(), "Processing text file please wait..", rawData.size());
		this.barProgressDialog.show();
		new Thread(new C00381(rawData)).start();
	}

	private Consumer listToConsumer(String rawData) {
		String[] data = StringManager.listTrimmer(rawData.split("~"));
		Consumer consumer = new Consumer();
		consumer.setAccountNumber(data[0]);
		consumer.setConnCode(data[1]);
		consumer.setInitialReading(Double.parseDouble(data[2].trim().replace(",", "")));
		consumer.setMeterSerial(data[3]);
		consumer.setRateCode(data[4]);
		consumer.setName(data[5]);
		consumer.setAddress(data[6]);
		consumer.setContracted(data[7].equals("Y"));
		try {
			consumer.setMultiplier(Double.parseDouble(data[8]));
		} catch (NumberFormatException e) {
			consumer.setMultiplier(0.0); // or any sensible default
			Log.e("ParseError", "Invalid multiplier value: " + data[8]);
		}
		consumer.setCoreLoss(Double.parseDouble(data[43]));
		consumer.setTransformerLostTestResult(Double.parseDouble(data[44]));
		consumer.setDemandMultiplier(Double.parseDouble(data[49]));
		consumer.setDemandMin(Double.parseDouble(data[50]));
		consumer.setDemandMax(Double.parseDouble(data[51]));
		consumer.setArMats(Double.parseDouble(data[56].replace(",", "")));
		consumer.setScap(Double.parseDouble(data[57].replace(",", "")));
		consumer.setRefund(Double.parseDouble(data[58].replace(",", "")));
		consumer.setHelp(Double.parseDouble(data[59].replace(",", "")));
		consumer.setPilfer(Double.parseDouble(data[60].replace(",", "")));
		consumer.setSCSwitch(data[61].equals("T"));
		consumer.setNumberOfArrears(Integer.parseInt(data[62]));
		consumer.setArrears(Double.parseDouble(data[63].replace(",", "")));
		consumer.setAveKwh(Double.parseDouble(data[80].replace(",", "")));
		consumer.setDateEnergized(data[81]);
		consumer.setMeterBrand(data[82]);
		consumer.setTransformerNumber(data[83]);
		consumer.setKw(Double.parseDouble(data[84].trim()));
		consumer.setTransformerRental(Double.parseDouble(data[86].trim().replace(",", "")));
		consumer.setDemandCharge(Double.parseDouble(data[85].trim().replace(",", "")));
		consumer.setDisco(Double.parseDouble(data[87].trim().replace(",", "")));
		consumer.setIncentives(Double.parseDouble(data[88].trim().replace(",", "")));
		consumer.setMaterial(Double.parseDouble(data[89].trim().replace(",", "")));
		consumer.setEquiptment(Double.parseDouble(data[90].trim().replace(",", "")));
		consumer.setOthersSurcharge(Double.parseDouble(data[91].trim().replace(",", "")));
		consumer.setdaaRefund(Double.parseDouble(data[97].replace(",", "")));
		consumer.setlocalFranchiseTax(Double.parseDouble(data[98].replace(",", "")));
		consumer.setrptprevTax(Double.parseDouble(data[99].trim().replace(",", "")));

		Rates rate = this.dsRates.getConsumerRate(consumer.getRateCode());
		if (rate.getId() == -1) {
			rate.setScSwitch(Boolean.valueOf(consumer.isScSwitch()));
			rate.setRateCode(consumer.getRateCode());
			rate.setGenSys(Double.parseDouble(data[9]));
			rate.setHostComm(Double.parseDouble(data[REQUEST_LOAD]));
			rate.setIcera(Double.parseDouble(data[11].trim().replace(",", "")));
			rate.setTcDemand(Double.parseDouble(data[12]));
			rate.setTcSystem(Double.parseDouble(data[13]));
			rate.setSystemLoss(Double.parseDouble(data[14]));
			rate.setDcDemand(Double.parseDouble(data[15]));
			rate.setDcDistribution(Double.parseDouble(data[16]));
			rate.setScSupplySys(Double.parseDouble(data[17]));
			rate.setScRetailCust(Double.parseDouble(data[18]));
			rate.setMcSys(Double.parseDouble(data[19]));
			rate.setMcRetailCust(Double.parseDouble(data[REQUEST_SAVE]));
			rate.setUcsd(Double.parseDouble(data[23]));
			rate.setUcme(Double.parseDouble(data[24]));
			rate.setUcStrandedContractCost(Double.parseDouble(data[25]));
			rate.setUcec(Double.parseDouble(data[26]));
			rate.setFeedTariffAllowance(Double.parseDouble(data[27]));
			rate.setParr(Double.parseDouble(data[29]));
			rate.setLifeLineSubsidy(Double.parseDouble(data[30]));
			rate.setSeniorCitizenDiscount(Double.parseDouble(data[32]));
			rate.setSeniorCitizenSubsidy(Double.parseDouble(data[33]));
			rate.setfranchiseTax(Double.parseDouble(data[35]));
			rate.setPrevYearAdjPowerCost(Double.parseDouble(data[36]));
			rate.setReinvestmentFundSustCapex(Double.parseDouble(data[37]));
			rate.setVatGensys(Double.parseDouble(data[64]));
			rate.setVatPARR(Double.parseDouble(data[65]));
			rate.setVatIcera(Double.parseDouble(data[66]));
			rate.setVatTcSystem(Double.parseDouble(data[67]));
			rate.setVatTcDemand(Double.parseDouble(data[68]));
			rate.setVatDcDistribution(Double.parseDouble(data[69]));
			rate.setVatDcDemand(Double.parseDouble(data[70]));
			rate.setVatScSupply(Double.parseDouble(data[71]));
			rate.setVatMcSystem(Double.parseDouble(data[72]));
			rate.setVatLifelineSubsidy(Double.parseDouble(data[73]));
			rate.setVatReinvestmentFundSustCapex(Double.parseDouble(data[74]));
			rate.setVatSeniorCitizen(Double.parseDouble(data[75]));
			rate.setVatScRetail(Double.parseDouble(data[76]));
			rate.setVatMcRetail(Double.parseDouble(data[77]));
			rate.setVatSystemLoss(Double.parseDouble(data[78]));
			rate.setVatSystemLossTransmission(Double.parseDouble(data[79]));
			rate.setUcmeRed(Double.parseDouble(data[92]));
			rate.setRealPropertyTax(Double.parseDouble(data[96]));
			this.dsRates.createRates(rate);
		}
		return consumer;
	}

	protected UserProfile listToUserProfile(String dataValues) {
		String[] data = StringManager.listTrimmer(dataValues.split("~"));
		UserProfile up = new UserProfile();
		up.setRoute(data[0]);
		up.setReadingDate(data[1]);
		up.setInitialReadingDate(data[2]);
		up.setName(data[3]);
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