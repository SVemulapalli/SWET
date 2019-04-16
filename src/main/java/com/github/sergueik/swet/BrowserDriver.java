package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import com.github.sergueik.swet.OSUtils;

/**
 * Browser Driver wrapper class for Selenium WebDriver Elementor Tool(SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
// TODO: Exception in thread "main" java.lang.NoSuchMethodError:
// com.google.common.base.Preconditions.checkState(ZLjava/lang/String;Ljava/lang/Object;)V
// at
// org.openqa.selenium.remote.service.DriverService.checkExecutable(DriverService.java:136)
// guava 21,22,23,24-jre
@SuppressWarnings("deprecation")
public class BrowserDriver {

	public static WebDriver driver;
	private static String osName = OSUtils.getOsName();
	private static String location = "";
	private static String propertiesFileName = "application.properties";
	private static String configurationFileName = "test.configuration";

	private static StringBuilder loggingSb = new StringBuilder();
	private static Formatter formatter = new Formatter(loggingSb, Locale.US);
	private static String applicationChromeDriverPath;
	private static String applicationGeckoDriverPath;
	private static String applicationFirefoxBrowserPath;
	private static String applicationIeDriverPath;
	private static String applicationEdgeDriverPath;
	private static Map<String, String> propertiesMap;

	@SuppressWarnings("deprecation")
	public static WebDriver initialize(String browser) {

		propertiesMap = PropertiesParser
				.getProperties(String.format("%s/src/main/resources/%s",
						System.getProperty("user.dir"), propertiesFileName));
		applicationChromeDriverPath = propertiesMap.get("chromeDriverPath");
		applicationGeckoDriverPath = propertiesMap.get("geckoDriverPath");
		applicationFirefoxBrowserPath = propertiesMap.get("firefoxBrowserPath");
		applicationIeDriverPath = propertiesMap.get("ieDriverPath");
		applicationEdgeDriverPath = propertiesMap.get("edgeDriverPath");

		TestConfigurationParser
				.getConfiguration(String.format("%s/src/main/resources/%s",
						System.getProperty("user.dir"), configurationFileName));

		DesiredCapabilities capabilities = null;
		browser = browser.toLowerCase();
		if (browser.equals("firefox")) {
			capabilities = capabilitiesFirefox();
		} else if (browser.equals("chrome")) {
			capabilities = capabilitiesChrome();
		} else if (browser.equals("internet explorer")) {
			capabilities = capabilitiesInternetExplorer();
		} else if (browser.equals("egde")) {
			capabilities = capabilitiesEdge();
		} else if (browser.equals("android")) {
			capabilities = capabilitiesAndroid();
		} else if (browser.equals("safari")) {
			capabilities = capabilitiesSafari();
		} else if (browser.equals("iphone")) {
			capabilities = capabilitiesiPhone();
		} else if (browser.equals("ipad")) {
			capabilities = capabilitiesiPad();
		}
		if (location.toLowerCase().contains("http:")) {
			try {
				// log.info("Running on Selenium Grid: " + location);
				driver = new RemoteWebDriver(new URL(location), capabilities);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		} else if (browser.equals("firefox")) {
			driver = new FirefoxDriver(capabilities);
		} else if (browser.equals("safari")) {
			SafariOptions options = new SafariOptions();
			driver = new SafariDriver(options);
		} else if (browser.equals("edge")) {
			driver = new EdgeDriver(capabilities);
		} else if (browser.equals("chrome")) {
			// Exception in thread "main" java.lang.NoSuchMethodError: com.google.common.base.Preconditions.checkState(ZLjava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V at org.openqa.selenium.remote.service.DriverService.findExecutable(DriverService.java:125)
			driver = new ChromeDriver(capabilities);
		} else if (browser.equals("internet explorer")) {
			driver = new InternetExplorerDriver(capabilities);
		} else if (browser.equals("android")) {
			driver = new ChromeDriver(capabilities);
		} else if (browser.equals("iphone")) {
			driver = new ChromeDriver(capabilities);
		} else if (browser.equals("ipad")) {
			driver = new ChromeDriver(capabilities);
		}
		// ngDriver = new NgWebDriver(driver);
		return driver;
	}

	private static DesiredCapabilities capabilitiesSafari() {
		DesiredCapabilities capabilities = DesiredCapabilities.safari();
		SafariOptions options = new SafariOptions();
		// TODO: need to conditionally compile:
		// With Selenium 3.13.x
		// setUseCleanSession
		// no longer not exist in org.openqa.selenium.safari.SafariOptions
		// options.setUseCleanSession(true);
		capabilities.setCapability(SafariOptions.CAPABILITY, options);
		return capabilities;
	}

	private static DesiredCapabilities capabilitiesAndroid() {
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();

		Map<String, String> mobileEmulation = new HashMap<>();
		mobileEmulation.put("deviceName", "Google Nexus 5");

		Map<String, Object> chromeOptions = new HashMap<>();
		chromeOptions.put("mobileEmulation", mobileEmulation);
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

		return capabilities;
	}

	private static DesiredCapabilities capabilitiesiPhone() {
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();

		Map<String, String> mobileEmulation = new HashMap<>();
		mobileEmulation.put("deviceName", "Apple iPhone 6");

		Map<String, Object> chromeOptions = new HashMap<>();
		chromeOptions.put("mobileEmulation", mobileEmulation);
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

		return capabilities;
	}

	private static DesiredCapabilities capabilitiesiPad() {
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();

		Map<String, String> mobileEmulation = new HashMap<>();
		mobileEmulation.put("deviceName", "Apple iPad");

		Map<String, Object> chromeOptions = new HashMap<>();
		chromeOptions.put("mobileEmulation", mobileEmulation);
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

		return capabilities;
	}

	@SuppressWarnings("deprecation")
	private static DesiredCapabilities capabilitiesEdge() {
		// NOTE: the version of edge webdriver varies with the Windows 10 build
		// version
		// https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
		// https://www.cloudenablers.com/blog/selenium-script-for-microsoft-edge-in-windows-10/
		final String edgeDriverPath = (applicationEdgeDriverPath == null)
				? "C:\\Program Files (x86)\\Microsoft Web Driver\\MicrosoftWebDriver.exe"
				: applicationEdgeDriverPath;
		System.setProperty("webdriver.edge.driver",
				new File(edgeDriverPath).getAbsolutePath());
		DesiredCapabilities capabilities = DesiredCapabilities.edge();
		return capabilities;
	}

	@SuppressWarnings("deprecation")
	private static DesiredCapabilities capabilitiesFirefox() {

		final String geckoDriverPath = (applicationGeckoDriverPath == null)
				? osName.toLowerCase().startsWith("windows")
						? "c:/java/selenium/geckodriver.exe" : "/var/run/geckodriver"
				: applicationGeckoDriverPath;
		// firefox.browser.path
		final String firefoxBrowserPath = (applicationFirefoxBrowserPath == null)
				? osName.toLowerCase().startsWith("windows")
						? "c:/Program Files (x86)/Mozilla Firefox/firefox.exe"
						: osName.toLowerCase().startsWith("mac")
								? "/Applications/Firefox.app/Contents/MacOS/firefox.bin"
								: "/usr/bin/firefox/firefox"
				: applicationFirefoxBrowserPath;
		System.setProperty("webdriver.gecko.driver",
				new File(geckoDriverPath).getAbsolutePath());
		System.setProperty("webdriver.firefox.bin",
				new File(firefoxBrowserPath).getAbsolutePath());
		System.setProperty("webdriver.reap_profile", "false");
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();

		// TODO: switch to Selenium 3.X+
		/*
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(new File(firefoxBrowserPath).getAbsolutePath());
		
		capabilities.setCapability("moz:firefoxOptions", firefoxOptions);
		*/
		capabilities.setCapability("firefox_binary",
				new File(firefoxBrowserPath).getAbsolutePath());

		capabilities.setCapability("marionette", false);
		FirefoxProfile profile = new FirefoxProfile();
		// no longer exists in Selenium
		// profile.setEnableNativeEvents(true);
		profile.setAcceptUntrustedCertificates(true);
		profile.setAssumeUntrustedCertificateIssuer(false);

		// Disable Firefox Auto-Updating
		profile.setPreference("app.update.auto", false);
		profile.setPreference("app.update.enabled", false);

		capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		capabilities.setCapability("elementScrollBehavior", 1);
		capabilities.setBrowserName(DesiredCapabilities.firefox().getBrowserName());
		return capabilities;
	}

	// https://sites.google.com/a/chromium.org/chromedriver/capabilities
	// http://peter.sh/experiments/chromium-command-line-switches/
	// https://developer.chrome.com/extensions/contentSettings
	// http://www.programcreek.com/java-api-examples/index.php?api=org.openqa.selenium.chrome.ChromeOptions
	private static DesiredCapabilities capabilitiesChrome() {

		final String chromeDriverPath = (applicationChromeDriverPath == null)
				? osName.toLowerCase().startsWith("windows")
						? "c:/java/selenium/chromedriver.exe" : "/var/run/chromedriver"
				: applicationChromeDriverPath;
		System.setProperty("webdriver.chrome.driver",
				new File(chromeDriverPath).getAbsolutePath());
		// "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"

		DesiredCapabilities capabilities = DesiredCapabilities.chrome();

		ChromeOptions options = new ChromeOptions();

		Map<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		String downloadFilepath = System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "target"
				+ System.getProperty("file.separator");
		chromePrefs.put("download.default_directory", downloadFilepath);
		chromePrefs.put("enableNetwork", "true");
		// ignore failures in unsupported methods with Selenium 3.8.1
		try {
			options.addArguments("allow-running-insecure-content");
			options.addArguments("allow-insecure-localhost");
			options.addArguments("enable-local-file-accesses");
			options.addArguments("disable-notifications");
			// options.addArguments("start-maximized");
			options.addArguments("browser.download.folderList=2");
			options.addArguments(
					"--browser.helperApps.neverAsk.saveToDisk=image/jpg,text/csv,text/xml,application/xml,application/vnd.ms-excel,application/x-excel,application/x-msexcel,application/excel,application/pdf");
			options.addArguments("browser.download.dir=" + downloadFilepath);
			// options.addArguments("user-data-dir=/path/to/your/custom/profile");

		} catch (java.lang.NoSuchMethodError e) {
			System.err.println("Exception (ignored) " + e.toString());
		}

		try {
			options.setExperimentalOption("prefs", chromePrefs);
		} catch (java.lang.NoSuchMethodError e) {
			System.err.println("Exception (ignored) " + e.toString());
		}
		capabilities.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
		// debugging
		try {
			// capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			// surprisingly the exception get caught but thrown again.
		} catch (Exception e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		return capabilities;
	}

	// https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver
	private static DesiredCapabilities capabilitiesInternetExplorer() {

		DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
		final String ieDriverPath = (applicationIeDriverPath == null)
				? "c:/java/selenium/IEDriverServer.exe" : applicationIeDriverPath;
		System.setProperty("webdriver.ie.driver", ieDriverPath
		/* (new File(ieDriverPath)).getAbsolutePath() */);
		capabilities.setCapability(
				InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
				true);
		capabilities.setCapability("ignoreZoomSetting", true);
		capabilities.setCapability("ignoreProtectedModeSettings", true);
		capabilities.setCapability("requireWindowFocus", true);
		capabilities.setBrowserName(
				DesiredCapabilities.internetExplorer().getBrowserName());
		return capabilities;
	}

	public static void close() {
		driver.close();
		if (driver != null) {
			driver.quit();
		}
	}
}
