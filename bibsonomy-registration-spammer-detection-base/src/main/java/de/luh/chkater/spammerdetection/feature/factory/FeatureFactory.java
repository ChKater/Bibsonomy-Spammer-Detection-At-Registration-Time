package de.luh.chkater.spammerdetection.feature.factory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.model.User;

import com.google.common.net.InternetDomainName;

import de.luh.chkater.spammerdetection.feature.impl.CountFeature;
import de.luh.chkater.spammerdetection.feature.impl.InformationSuprise;
import de.luh.chkater.spammerdetection.feature.impl.Ratio;
import de.luh.chkater.spammerdetection.feature.interfaces.AbstractFeature;
import de.luh.chkater.spammerdetection.feature.interfaces.BooleanFeature;
import de.luh.chkater.spammerdetection.feature.interfaces.FeatureCategory;
import de.luh.chkater.spammerdetection.feature.interfaces.NominalFeature;
import de.luh.chkater.spammerdetection.feature.interfaces.NumericFeature;
import de.luh.chkater.spammerdetection.keyboard.DVORAK;
import de.luh.chkater.spammerdetection.keyboard.KeyBoardLayout;
import de.luh.chkater.spammerdetection.keyboard.QwertzDe;
import de.luh.chkater.spammerdetection.log.LoggedRegistration;
import de.luh.chkater.spammerdetection.model.InteractionEvent;
import de.luh.chkater.spammerdetection.model.LoggedInteractionFactory;
import de.luh.chkater.spammerdetection.regLog.KeyboardStatistic;
import de.luh.chkater.spammerdetection.regLog.KeyboardStatisticFactory;
import de.luh.chkater.spammerdetection.regLog.MouseStatistic;
import de.luh.chkater.spammerdetection.regLog.MouseStatisticFactory;
import de.luh.chkater.spammerdetection.transformation.ContainsDigit;
import de.luh.chkater.spammerdetection.transformation.CountInString;
import de.luh.chkater.spammerdetection.transformation.DayOfWeek;
import de.luh.chkater.spammerdetection.transformation.DistanceOnKeyboard;
import de.luh.chkater.spammerdetection.transformation.Entropy;
import de.luh.chkater.spammerdetection.transformation.Equal;
import de.luh.chkater.spammerdetection.transformation.HourOfDay;
import de.luh.chkater.spammerdetection.transformation.IP2Country;
import de.luh.chkater.spammerdetection.transformation.IsMailFromUniversity;
import de.luh.chkater.spammerdetection.transformation.Length;
import de.luh.chkater.spammerdetection.transformation.MaximumTimesALetterRepeated;
import de.luh.chkater.spammerdetection.transformation.NumberOfStartingDigits;
import de.luh.chkater.spammerdetection.transformation.NumberOfUniqueAlphabetLetters;
import de.luh.chkater.spammerdetection.transformation.PercentageOfConsecutiveKeys;
import de.luh.chkater.spammerdetection.transformation.PercentageOfKeys;
import de.luh.chkater.spammerdetection.transformation.ProportionOfDigits;
import de.luh.chkater.spammerdetection.transformation.TransformToLocalTime;
import de.luh.chkater.spammerdetection.transformation.log.BrowserFromUseragent;
import de.luh.chkater.spammerdetection.transformation.log.OSFromUserAgent;
import de.luh.chkater.spammerdetection.transformation.log.TimeBetween;
import de.luh.chkater.spammerdetection.utility.IPSelector;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * Factory for holding all Features by name and category.
 *
 * @author Christian Kater
 */
public class FeatureFactory {

	private static final String NOT_A_NUMBER = "[^0-9]";
	private static final String NOT_A_LETTER = "[^a-z]";
	private static final KeyBoardLayout QWERTY_DE = new QwertzDe();
	private static final KeyBoardLayout DVORAK_US = new DVORAK();
	private static final int NUMBER_OF_DIRECTIONS = 8;

	private static BooleanFeature spammer = new BooleanFeature("spammer", 1, FeatureCategory.ATTRIBUTE, false,
			new Function<User, Boolean>() {

				@Override
				public Boolean apply(User user) {
					return new Boolean(user.isSpammer());
				}
			});

	private static List<String> getBrowserValues() {
		Set<String> browserValues = new HashSet<>();
		for (Browser browser : Browser.values()) {
			browserValues.add(browser.getName());
		}
		return new ArrayList<>(browserValues);
	}

	private static List<String> getOSValues() {
		Set<String> osValues = new HashSet<>(OperatingSystem.values().length);
		for (OperatingSystem os : OperatingSystem.values()) {
			osValues.add(os.getName());
		}
		return new ArrayList<>(osValues);
	}

	private static class FeatureFactoryHolder {
		private static final FeatureFactory INSTANCE = new FeatureFactory();
	}

	private List<AbstractFeature> features;

	private FeatureFactory() {
		features = new ArrayList<>();

		addFeature(spammer);
		addFeature(new NominalFeature("country", 1, FeatureCategory.ENVIRONMENT, false, IP2Country.countryIsoCodes,
				new Function<User, String>() {
					IP2Country ip2country = new IP2Country();

					@Override
					public String apply(User user) {
						return ip2country.apply(IPSelector.selectIPAdress(user.getIPAddress()));
					}
				}));

		addFeature(new BooleanFeature("uniMail", 1, FeatureCategory.ENVIRONMENT, false, new Function<User, Boolean>() {
			IsMailFromUniversity isMailFromUniversity = new IsMailFromUniversity();

			@Override
			public Boolean apply(User user) {
				return isMailFromUniversity.apply(user.getEmail());
			}
		}));
		addFeature(new NominalFeature("regDay", 1, FeatureCategory.ENVIRONMENT, false,
				Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7"), new Function<User, String>() {

					TransformToLocalTime toLocalTime = new TransformToLocalTime();
					DayOfWeek dOW = new DayOfWeek();

					@Override
					public String apply(User user) {
						return dOW.apply(toLocalTime.apply(user.getRegistrationDate(),
								IPSelector.selectIPAdress(user.getIPAddress())));
					}
				}));
		addFeature(
				new NumericFeature("realnameParts", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

					@Override
					public Double apply(User user) {
						String realname = user.getRealname();
						if (realname == null) {
							return null;
						}
						String[] split = realname.split(" ");
						if (split == null) {
							return null;
						}
						return new Double(split.length);
					}
				}));
		addFeature(new BooleanFeature("nameDigit", 1, FeatureCategory.LANGUAGE, false, new Function<User, Boolean>() {

			ContainsDigit contains = new ContainsDigit();

			@Override
			public Boolean apply(User user) {
				return contains.apply(user.getName());
			}
		}));
		addFeature(new BooleanFeature("emailDigit", 1, FeatureCategory.LANGUAGE, false, new Function<User, Boolean>() {

			ContainsDigit contains = new ContainsDigit();

			@Override
			public Boolean apply(User user) {
				return contains.apply(user.getEmail());
			}
		}));
		addFeature(
				new BooleanFeature("realnameDigit", 1, FeatureCategory.LANGUAGE, false, new Function<User, Boolean>() {

					ContainsDigit contains = new ContainsDigit();

					@Override
					public Boolean apply(User user) {
						return contains.apply(user.getRealname());
					}
				}));
		addFeature(
				new BooleanFeature("homepageDigit", 1, FeatureCategory.LANGUAGE, false, new Function<User, Boolean>() {

					ContainsDigit contains = new ContainsDigit();

					@Override
					public Boolean apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return contains.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("nameLength", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

			Length length = new Length();

			@Override
			public Double apply(User user) {
				return length.apply(user.getName());
			}
		}));
		addFeature(new NumericFeature("emailLength", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

			Length length = new Length();

			@Override
			public Double apply(User user) {
				return length.apply(user.getEmail());
			}
		}));
		addFeature(
				new NumericFeature("realnameLength", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

					Length length = new Length();

					@Override
					public Double apply(User user) {
						return length.apply(user.getRealname());
					}
				}));
		addFeature(
				new NumericFeature("homepageLength", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

					Length length = new Length();

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return length.apply(homepage.toExternalForm());
					}
				}));
		addFeature(
				new NumericFeature("namePropDigit", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

					ProportionOfDigits pod = new ProportionOfDigits();

					@Override
					public Double apply(User user) {
						return pod.apply(user.getName());
					}
				}));
		addFeature(
				new NumericFeature("emailPropDigit", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

					ProportionOfDigits pod = new ProportionOfDigits();

					@Override
					public Double apply(User user) {
						return pod.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("realnamePropDigit", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					ProportionOfDigits pod = new ProportionOfDigits();

					@Override
					public Double apply(User user) {
						return pod.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("homepagePropDigit", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					ProportionOfDigits pod = new ProportionOfDigits();

					@Override
					public Double apply(User user) {
						if (user.getHomepage() == null) {
							return null;
						}
						return pod.apply(user.getHomepage().toExternalForm());
					}
				}));
		addFeature(
				new BooleanFeature("nameEqualMail", 1, FeatureCategory.LANGUAGE, false, new Function<User, Boolean>() {

					Equal equal = new Equal();

					@Override
					public Boolean apply(User user) {
						if (user.getName() == null || user.getEmail() == null) {
							return null;
						}
						return equal.apply(user.getName(), user.getEmail().split("@")[0]);
					}
				}));
		addFeature(new BooleanFeature("nameEqualMailEqualRealnname", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Boolean>() {

					Equal equal = new Equal();

					@Override
					public Boolean apply(User user) {
						if (user.getName() == null || user.getEmail() == null || user.getRealname() == null) {
							return null;
						}
						Boolean nameEmail = equal.apply(user.getName(), user.getEmail().split("@")[0]);
						if (nameEmail == null) {
							return null;
						}
						Boolean nameRealname = equal.apply(user.getName(), user.getRealname());
						if (nameRealname == null) {
							return null;
						}
						return new Boolean(nameEmail && nameRealname);
					}
				}));
		addFeature(new NumericFeature("nameNumDigit", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

			CountInString count = new CountInString();

			@Override
			public Double apply(User user) {
				return count.apply(user.getName(), NOT_A_NUMBER);
			}
		}));
		addFeature(
				new NumericFeature("emailNumDigit", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

					CountInString count = new CountInString();

					@Override
					public Double apply(User user) {
						return count.apply(user.getEmail(), NOT_A_NUMBER);
					}
				}));
		addFeature(new NumericFeature("realnameNumDigit", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					CountInString count = new CountInString();

					@Override
					public Double apply(User user) {
						return count.apply(user.getRealname(), NOT_A_NUMBER);
					}
				}));
		addFeature(new NumericFeature("homepageNumDigit", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					CountInString count = new CountInString();

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return count.apply(homepage.toExternalForm(), NOT_A_NUMBER);
					}
				}));

		addFeature(new CountFeature("spamIP", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				String ip = user.getIPAddress();
				if (ip == null) {
					return null;
				}

				return IPSelector.selectIPAdress(ip);
			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return !user.isSpammer();
			}
		}));

		addFeature(new CountFeature("nameCount", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {
					String name = user.getName();
					if (name == null) {
						return null;
					}
					return name.toLowerCase().replaceAll(NOT_A_LETTER, "");
				} catch (Exception e) {
					return null;
				}
			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return false;
			}
		}));

		addFeature(new CountFeature("emailCount", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {
					String email = user.getEmail().split("@")[0];
					if (email == null) {
						return null;
					}
					return email.toLowerCase().replaceAll(NOT_A_LETTER, "");
				} catch (Exception e) {
					return null;
				}
			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return false;
			}
		}));

		addFeature(new CountFeature("realnameCount", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {
					String realname = user.getRealname();
					if (realname == null) {
						return null;
					}
					return realname.toLowerCase().replaceAll(NOT_A_LETTER, "");
				} catch (Exception e) {
					return null;
				}
			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return false;
			}
		}));

		addFeature(
				new CountFeature("firstnameCount", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

					@Override
					public String apply(User user) {
						try {
							String firstname = user.getRealname().split(" ")[0];
							if (firstname == null) {
								return null;
							}
							return firstname.toLowerCase().replaceAll(NOT_A_LETTER, "");
						} catch (Exception e) {
							return null;
						}
					}
				}, new Function<User, Boolean>() {

					@Override
					public Boolean apply(User user) {
						return false;
					}
				}));

		addFeature(new CountFeature("lastnameCount", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {
					String[] lastnameParts = user.getRealname().split(" ");
					if (lastnameParts.length <= 1) {
						return null;
					}
					String lastname = lastnameParts[lastnameParts.length - 1];
					if (lastname == null) {
						return null;
					}
					return lastname.toLowerCase().replaceAll(NOT_A_LETTER, "");
				} catch (Exception e) {
					return null;
				}
			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return false;
			}
		}));

		addFeature(new CountFeature("domainCount", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {

					URL homepage = user.getHomepage();
					if (homepage == null) {
						return null;
					}
					return homepage.getHost();
				} catch (Exception e) {
					return null;
				}

			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return false;
			}
		}));
		addFeature(new CountFeature("tldCount", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {
					URL homepage = user.getHomepage();
					if (homepage == null) {
						return null;
					}
					return InternetDomainName.from(homepage.getHost()).publicSuffix().toString();
				} catch (Exception e) {
					return null;
				}

			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return false;
			}
		}));

		/*
		 * 
		 */

		addFeature(new Ratio("ipRatio", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				String ip = user.getIPAddress();
				if (ip == null) {
					return null;
				}

				return IPSelector.selectIPAdress(ip);
			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return !user.isSpammer();
			}
		}));
		addFeature(new Ratio("realnameRatio", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {
					String realname = user.getRealname();
					if (realname == null) {
						return null;
					}
					return realname.toLowerCase().replaceAll(NOT_A_LETTER, "");
				} catch (Exception e) {
					return null;
				}
			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return !user.isSpammer();
			}
		}));
		addFeature(new Ratio("nameRatio", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {
					String name = user.getName();
					if (name == null) {
						return null;
					}
					return name.toLowerCase().replaceAll(NOT_A_LETTER, "");
				} catch (Exception e) {
					return null;
				}
			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return !user.isSpammer();
			}
		}));

		addFeature(new Ratio("emailRatio", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {
					String email = user.getEmail().split("@")[0];
					if (email == null) {
						return null;
					}
					return email.toLowerCase().replaceAll(NOT_A_LETTER, "");
				} catch (Exception e) {
					return null;
				}
			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return false;
			}
		}));

		addFeature(new Ratio("firstnameRatio", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {
					String firstname = user.getRealname().split(" ")[0];
					if (firstname == null) {
						return null;
					}
					return firstname.toLowerCase().replaceAll(NOT_A_LETTER, "");
				} catch (Exception e) {
					return null;
				}
			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return !user.isSpammer();
			}
		}));

		addFeature(new Ratio("lastnameRatio", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {
					String[] lastnameParts = user.getRealname().split(" ");
					String lastname = lastnameParts[lastnameParts.length - 1];
					if (lastnameParts.length <= 1) {
						return null;
					}
					if (lastname == null) {
						return null;
					}
					return lastname.toLowerCase().replaceAll(NOT_A_LETTER, "");
				} catch (Exception e) {
					return null;
				}
			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return !user.isSpammer();
			}
		}));

		addFeature(new Ratio("domainRatio", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {

					URL homepage = user.getHomepage();
					if (homepage == null) {
						return null;
					}
					return homepage.getHost();
				} catch (Exception e) {
					return null;
				}

			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return !user.isSpammer();
			}
		}));
		addFeature(new Ratio("tldRatio", 1, FeatureCategory.POPULATION_BASED, new Function<User, String>() {

			@Override
			public String apply(User user) {
				try {
					URL homepage = user.getHomepage();
					if (homepage == null) {
						return null;
					}
					return InternetDomainName.from(homepage.getHost()).publicSuffix().toString();
				} catch (Exception e) {
					return null;
				}

			}
		}, new Function<User, Boolean>() {

			@Override
			public Boolean apply(User user) {
				return !user.isSpammer();
			}
		}));

		addFeature(new NominalFeature("regHour", 1,
				FeatureCategory.ENVIRONMENT, false, Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
						"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"),
				new Function<User, String>() {

					HourOfDay hod = new HourOfDay();
					TransformToLocalTime toLocalTime = new TransformToLocalTime();

					@Override
					public String apply(User user) {
						return hod.apply(toLocalTime.apply(user.getRegistrationDate(),
								IPSelector.selectIPAdress(user.getIPAddress())));
					}
				}));
		addFeature(
				new NumericFeature("activationDuration", 1, FeatureCategory.LOG, false, new Function<User, Double>() {

					TimeBetween tb = new TimeBetween();

					@Override
					public Double apply(User user) {
						LoggedRegistration loggedRegistration = LoggedRegistration.get(user.getName());
						if (loggedRegistration == null) {
							return null;
						}
						return tb.apply(loggedRegistration.getActivation(),
								loggedRegistration.getRegistrationSuccess());
					}
				}));
		addFeature(
				new NumericFeature("registrationDuration", 1, FeatureCategory.LOG, false, new Function<User, Double>() {

					TimeBetween tb = new TimeBetween();

					@Override
					public Double apply(User user) {
						LoggedRegistration loggedRegistration = LoggedRegistration.get(user.getName());
						if (loggedRegistration == null) {
							return null;
						}
						return tb.apply(loggedRegistration.getRegistrationStart(),
								loggedRegistration.getRegistrationEnd());
					}
				}));
		addFeature(new NominalFeature("browser", 1, FeatureCategory.LOG, false, getBrowserValues(),
				new Function<User, String>() {

					BrowserFromUseragent bfu = new BrowserFromUseragent();

					@Override
					public String apply(User user) {
						LoggedRegistration loggedRegistration = LoggedRegistration.get(user.getName());
						if (loggedRegistration == null) {
							return null;
						}
						return bfu.apply(UserAgent.parseUserAgentString(loggedRegistration.getUserAgent()));
					}
				}));
		addFeature(new NominalFeature("os", 1, FeatureCategory.LOG, false, getOSValues(), new Function<User, String>() {

			OSFromUserAgent ofu = new OSFromUserAgent();

			@Override
			public String apply(User user) {
				LoggedRegistration loggedRegistration = LoggedRegistration.get(user.getName());
				if (loggedRegistration == null) {
					return null;
				}
				return ofu.apply(UserAgent.parseUserAgentString(loggedRegistration.getUserAgent()));
			}
		}));
		addFeature(new BooleanFeature("refererExists", 1, FeatureCategory.LOG, false, new Function<User, Boolean>() {
			@Override
			public Boolean apply(User user) {
				LoggedRegistration loggedRegistration = LoggedRegistration.get(user.getName());
				if (loggedRegistration == null) {
					return null;
				}
				URL referer = null;
				try {
					referer = new URL(loggedRegistration.getReferer());
				} catch (MalformedURLException e) {
					return null;
				}
				return !referer.getHost().equals("-");
			}
		}));
		addFeature(new BooleanFeature("refererFromBibsonomy", 1, FeatureCategory.LOG, false,
				new Function<User, Boolean>() {
					@Override
					public Boolean apply(User user) {
						LoggedRegistration loggedRegistration = LoggedRegistration.get(user.getName());
						if (loggedRegistration == null) {
							return null;
						}
						URL referer = null;
						try {
							referer = new URL(loggedRegistration.getReferer());
						} catch (MalformedURLException e) {
							return null;
						}
						return referer.getHost().equals("www.bibsonomy.org");
					}
				}));
		addFeature(new NumericFeature("nameNumStartingDigits", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfStartingDigits nosd = new NumberOfStartingDigits();

					@Override
					public Double apply(User user) {
						return nosd.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("realnameNumStartingDigits", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfStartingDigits nosd = new NumberOfStartingDigits();

					@Override
					public Double apply(User user) {
						return nosd.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("emailNumStartingDigits", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfStartingDigits nosd = new NumberOfStartingDigits();

					@Override
					public Double apply(User user) {
						return nosd.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("homepageNumStartingDigits", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfStartingDigits nosd = new NumberOfStartingDigits();

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return nosd.apply(homepage.getHost());
					}
				}));
		addFeature(new NumericFeature("namePropStartingDigits", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfStartingDigits nosd = new NumberOfStartingDigits();

					@Override
					public Double apply(User user) {
						return nosd.apply(user.getName()) / user.getName().length();
					}
				}));
		addFeature(new NumericFeature("realnamePropStartingDigits", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfStartingDigits nosd = new NumberOfStartingDigits();

					@Override
					public Double apply(User user) {
						if (user.getRealname() == null) {
							return null;
						}
						return nosd.apply(user.getRealname()) / user.getRealname().length();
					}
				}));
		addFeature(new NumericFeature("emailPropStartingDigits", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfStartingDigits nosd = new NumberOfStartingDigits();

					@Override
					public Double apply(User user) {
						if (user.getEmail() == null) {
							return null;
						}
						return nosd.apply(user.getEmail()) / user.getEmail().length();
					}
				}));
		addFeature(new NumericFeature("homepagePopStartingDigits", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfStartingDigits nosd = new NumberOfStartingDigits();

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return nosd.apply(homepage.getHost()) / homepage.getHost().length();
					}
				}));
		addFeature(new NumericFeature("nameNumUniqueAlphLetters", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfUniqueAlphabetLetters noual = new NumberOfUniqueAlphabetLetters();

					@Override
					public Double apply(User user) {
						return noual.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("realnameNumUniqueAlphLetters", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfUniqueAlphabetLetters noual = new NumberOfUniqueAlphabetLetters();

					@Override
					public Double apply(User user) {
						return noual.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("emailNumUniqueAlphLetters", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfUniqueAlphabetLetters noual = new NumberOfUniqueAlphabetLetters();

					@Override
					public Double apply(User user) {
						return noual.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("homepageNumUniqueAlphLetters", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					NumberOfUniqueAlphabetLetters noual = new NumberOfUniqueAlphabetLetters();

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return noual.apply(homepage.toString());
					}
				}));
		addFeature(new NumericFeature("nameNumMaxTimesLetterRep", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					MaximumTimesALetterRepeated mtalr = new MaximumTimesALetterRepeated();

					@Override
					public Double apply(User user) {
						return mtalr.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("realnameNumMaxTimesLetterRep", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					MaximumTimesALetterRepeated mtalr = new MaximumTimesALetterRepeated();

					@Override
					public Double apply(User user) {
						return mtalr.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("emailNumMaxTimesLetterRep", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					MaximumTimesALetterRepeated mtalr = new MaximumTimesALetterRepeated();

					@Override
					public Double apply(User user) {
						return mtalr.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("homepageNumMaxTimesLetterRep", 1, FeatureCategory.LANGUAGE, false,
				new Function<User, Double>() {

					MaximumTimesALetterRepeated mtalr = new MaximumTimesALetterRepeated();

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return mtalr.apply(homepage.toString());
					}
				}));
		addFeature(new NumericFeature("nameProp1RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.FIRST_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("nameProp2RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.SECOND_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("nameProp3RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.THIRD_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("nameProp4RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.FOURTH_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("emailProp1RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.FIRST_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailProp2RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.SECOND_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailProp3RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.THIRD_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailProp4RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.FOURTH_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("realnameProp1RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.FIRST_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnameProp2RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.SECOND_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnameProp3RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.THIRD_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnameProp4RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.FOURTH_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("homepageProp1RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.FIRST_ROW);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepageProp2RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.SECOND_ROW);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepageProp3RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.THIRD_ROW);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepageProp4RowQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToRows(), KeyBoardLayout.FOURTH_ROW);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));

		addFeature(new NumericFeature("namePropLittleFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropLittleFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropRingFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.RING_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropRingFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.RING_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropMiddleFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropMiddleFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropForefingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropForefingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));

		addFeature(new NumericFeature("realnamePropLittleFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropLittleFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropRingFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.RING_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropRingFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.RING_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropMiddleFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropMiddleFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropForefingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropForefingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));

		addFeature(new NumericFeature("emailPropLittleFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropLittleFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropRingFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.RING_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropRingFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.RING_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropMiddleFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropMiddleFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropForefingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropForefingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));

		addFeature(new NumericFeature("homepagePropLittleFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropLittleFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropRingFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.RING_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropRingFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.RING_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropMiddleFingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropMiddleFingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropForefingerLeftQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_LEFT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropForefingerRightQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(QWERTY_DE.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_RIGHT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));

		addFeature(new NumericFeature("namePropSameFingerAsPrevQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(QWERTY_DE.keyToFinger());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("emailPropSameFingerAsPrevQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(QWERTY_DE.keyToFinger());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("realnamePropSameFingerAsPrevQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(QWERTY_DE.keyToFinger());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("homepagePropSameFingerAsPrevQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(QWERTY_DE.keyToFinger());

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pokk.apply(homepage.toExternalForm());
					}
				}));

		addFeature(new NumericFeature("namePropSameHandAsPrevQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(QWERTY_DE.keyToHand());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("emailPropSameHandAsPrevQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(QWERTY_DE.keyToHand());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("realnamePropSameHandAsPrevQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(QWERTY_DE.keyToHand());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("homepagePropSameHandAsPrevQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(QWERTY_DE.keyToHand());

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pokk.apply(homepage.toExternalForm());
					}
				}));

		addFeature(new NumericFeature("nameDistanceOnKeyboardQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					DistanceOnKeyboard dok = new DistanceOnKeyboard(QWERTY_DE.keyToPosition());

					@Override
					public Double apply(User user) {
						return dok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("emailDistanceOnKeyboardQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					DistanceOnKeyboard dok = new DistanceOnKeyboard(QWERTY_DE.keyToPosition());

					@Override
					public Double apply(User user) {
						return dok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("realnameDistanceOnKeyboardQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					DistanceOnKeyboard dok = new DistanceOnKeyboard(QWERTY_DE.keyToPosition());

					@Override
					public Double apply(User user) {
						return dok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("homepageDistanceOnKeyboardQwertz", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					DistanceOnKeyboard dok = new DistanceOnKeyboard(QWERTY_DE.keyToPosition());

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return dok.apply(homepage.toExternalForm());
					}
				}));

		/*
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 */

		addFeature(new NumericFeature("nameProp1RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.FIRST_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("nameProp2RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.SECOND_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("nameProp3RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.THIRD_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("nameProp4RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.FOURTH_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("emailProp1RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.FIRST_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailProp2RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.SECOND_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailProp3RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.THIRD_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailProp4RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.FOURTH_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("realnameProp1RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.FIRST_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnameProp2RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.SECOND_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnameProp3RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.THIRD_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnameProp4RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.FOURTH_ROW);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("homepageProp1RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.FIRST_ROW);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepageProp2RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.SECOND_ROW);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepageProp3RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.THIRD_ROW);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepageProp4RowDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToRows(), KeyBoardLayout.FOURTH_ROW);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));

		addFeature(new NumericFeature("namePropLittleFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropLittleFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropRingFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.RING_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropRingFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.RING_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropMiddleFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropMiddleFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropForefingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("namePropForefingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getName());
					}
				}));

		addFeature(new NumericFeature("realnamePropLittleFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropLittleFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropRingFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.RING_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropRingFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.RING_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropMiddleFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropMiddleFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropForefingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("realnamePropForefingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getRealname());
					}
				}));

		addFeature(new NumericFeature("emailPropLittleFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropLittleFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropRingFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.RING_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropRingFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.RING_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropMiddleFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropMiddleFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropForefingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_LEFT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("emailPropForefingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_RIGHT);

					@Override
					public Double apply(User user) {
						return pok.apply(user.getEmail());
					}
				}));

		addFeature(new NumericFeature("homepagePropLittleFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropLittleFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.LITTLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropRingFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.RING_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropRingFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.RING_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropMiddleFingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_LEFT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropMiddleFingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.MIDDLE_FINGER_RIGHT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropForefingerLeftDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_LEFT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));
		addFeature(new NumericFeature("homepagePropForefingerRightDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfKeys pok = new PercentageOfKeys(DVORAK_US.keyToFinger(),
							KeyBoardLayout.FOREFINGERFINGER_RIGHT);

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pok.apply(homepage.toExternalForm());
					}
				}));

		addFeature(new NumericFeature("namePropSameFingerAsPrevDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(DVORAK_US.keyToFinger());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("emailPropSameFingerAsPrevDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(DVORAK_US.keyToFinger());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("realnamePropSameFingerAsPrevDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(DVORAK_US.keyToFinger());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("homepagePropSameFingerAsPrevDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(DVORAK_US.keyToFinger());

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pokk.apply(homepage.toExternalForm());
					}
				}));

		addFeature(new NumericFeature("namePropSameHandAsPrevDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(DVORAK_US.keyToHand());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("emailPropSameHandAsPrevDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(DVORAK_US.keyToHand());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("realnamePropSameHandAsPrevDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(DVORAK_US.keyToHand());

					@Override
					public Double apply(User user) {
						return pokk.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("homepagePropSameHandAsPrevDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					PercentageOfConsecutiveKeys pokk = new PercentageOfConsecutiveKeys(DVORAK_US.keyToHand());

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return pokk.apply(homepage.toExternalForm());
					}
				}));

		addFeature(new NumericFeature("nameDistanceOnKeyboardDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					DistanceOnKeyboard dok = new DistanceOnKeyboard(DVORAK_US.keyToPosition());

					@Override
					public Double apply(User user) {
						return dok.apply(user.getName());
					}
				}));
		addFeature(new NumericFeature("emailDistanceOnKeyboardDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					DistanceOnKeyboard dok = new DistanceOnKeyboard(DVORAK_US.keyToPosition());

					@Override
					public Double apply(User user) {
						return dok.apply(user.getEmail());
					}
				}));
		addFeature(new NumericFeature("realnameDistanceOnKeyboardDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					DistanceOnKeyboard dok = new DistanceOnKeyboard(DVORAK_US.keyToPosition());

					@Override
					public Double apply(User user) {
						return dok.apply(user.getRealname());
					}
				}));
		addFeature(new NumericFeature("homepageDistanceOnKeyboardDvorak", 1, FeatureCategory.KEYBOARD, false,
				new Function<User, Double>() {

					DistanceOnKeyboard dok = new DistanceOnKeyboard(DVORAK_US.keyToPosition());

					@Override
					public Double apply(User user) {
						URL homepage = user.getHomepage();
						if (homepage == null) {
							return null;
						}
						return dok.apply(homepage.toExternalForm());
					}
				}));

		addFeature(new InformationSuprise("nameInfoSupr", 1, FeatureCategory.LANGUAGE, new Function<User, String>() {

			@Override
			public String apply(User user) {
				return user.getName();
			}
		}));
		addFeature(new InformationSuprise("emailInfoSupr", 1, FeatureCategory.LANGUAGE, new Function<User, String>() {

			@Override
			public String apply(User user) {
				return user.getEmail();
			}
		}));
		addFeature(
				new InformationSuprise("realnameInfoSupr", 1, FeatureCategory.LANGUAGE, new Function<User, String>() {

					@Override
					public String apply(User user) {
						return user.getRealname();
					}
				}));
		addFeature(
				new InformationSuprise("homepageInfoSupr", 1, FeatureCategory.LANGUAGE, new Function<User, String>() {

					@Override
					public String apply(User user) {
						if (user.getHomepage() == null) {
							return null;
						}
						return user.getHomepage().toExternalForm();
					}
				}));
		addFeature(new NumericFeature("nameEntropy", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

			Entropy entropy = new Entropy();

			@Override
			public Double apply(User user) {
				// TODO Auto-generated method stub
				return entropy.apply(user.getName());
			}
		}));
		addFeature(new NumericFeature("emailEntropy", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

			Entropy entropy = new Entropy();

			@Override
			public Double apply(User user) {
				// TODO Auto-generated method stub
				return entropy.apply(user.getEmail());
			}
		}));
		addFeature(
				new NumericFeature("realnameEntropy", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

					Entropy entropy = new Entropy();

					@Override
					public Double apply(User user) {
						// TODO Auto-generated method stub
						return entropy.apply(user.getRealname());
					}
				}));
		addFeature(
				new NumericFeature("homepageEntropy", 1, FeatureCategory.LANGUAGE, false, new Function<User, Double>() {

					Entropy entropy = new Entropy();

					@Override
					public Double apply(User user) {
						if (user.getHomepage() == null) {
							return null;
						}
						return entropy.apply(user.getHomepage().toExternalForm());
					}
				}));

		// reg log features

//		addFeature(
//				new BooleanFeature("hasRegLog", 1, FeatureCategory.INTERACTION, false, new Function<User, Boolean>() {
//
//					@Override
//					public Boolean apply(User user) {
//						return user.getRegistrationLog() != null && user.getRegistrationLog().length() > 0;
//					}
//				}));
//		for (int i = 0; i < NUMBER_OF_DIRECTIONS; i++) {
//			// Mouse-Features
//			final int pos = i;
//			addFeature(new NumericFeature("minVelocity" + pos, 1, FeatureCategory.INTERACTION, false,
//					new Function<User, Double>() {
//
//						@Override
//						public Double apply(User user) {
//							if (user.getRegistrationLog() == null) {
//								return null;
//							}
//							MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//							if (statistic == null) {
//								return null;
//							}
//							return statistic.getMinVelocity()[pos];
//						}
//					}));
//			addFeature(new NumericFeature("maxVelocity" + pos, 1, FeatureCategory.INTERACTION, false,
//					new Function<User, Double>() {
//
//						@Override
//						public Double apply(User user) {
//							if (user.getRegistrationLog() == null) {
//								return null;
//							}
//							MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//							if (statistic == null) {
//								return null;
//							}
//							return statistic.getMaxVelocity()[pos];
//						}
//					}));
//			addFeature(new NumericFeature("meanVelocity" + pos, 1, FeatureCategory.INTERACTION, false,
//					new Function<User, Double>() {
//
//						@Override
//						public Double apply(User user) {
//							if (user.getRegistrationLog() == null) {
//								return null;
//							}
//							MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//							if (statistic == null) {
//								return null;
//							}
//							return statistic.getMeanVelocity()[pos];
//						}
//					}));
//
//			addFeature(new NumericFeature("minAcceleration" + pos, 1, FeatureCategory.INTERACTION, false,
//					new Function<User, Double>() {
//
//						@Override
//						public Double apply(User user) {
//							if (user.getRegistrationLog() == null) {
//								return null;
//							}
//							MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//							if (statistic == null) {
//								return null;
//							}
//							return statistic.getMinAcceleration()[pos];
//						}
//					}));
//			addFeature(new NumericFeature("maxAcceleration" + pos, 1, FeatureCategory.INTERACTION, false,
//					new Function<User, Double>() {
//
//						@Override
//						public Double apply(User user) {
//							if (user.getRegistrationLog() == null) {
//								return null;
//							}
//							MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//							if (statistic == null) {
//								return null;
//							}
//							return statistic.getMaxAcceleration()[pos];
//						}
//					}));
//			addFeature(new NumericFeature("meanAcceleration" + pos, 1, FeatureCategory.INTERACTION, false,
//					new Function<User, Double>() {
//
//						@Override
//						public Double apply(User user) {
//							if (user.getRegistrationLog() == null) {
//								return null;
//							}
//							MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//							if (statistic == null) {
//								return null;
//							}
//							return statistic.getMeanAcceleration()[pos];
//						}
//					}));
//			addFeature(new NumericFeature("movedDistance" + pos, 1, FeatureCategory.INTERACTION, false,
//					new Function<User, Double>() {
//
//						@Override
//						public Double apply(User user) {
//							if (user.getRegistrationLog() == null) {
//								return null;
//							}
//							MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//							if (statistic == null) {
//								return null;
//							}
//							return statistic.getMovedDistance()[pos];
//						}
//					}));
//
//		}
//		addFeature(new NumericFeature("mouseMoveTime", 1, FeatureCategory.INTERACTION, false,
//				new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMouseMoveTime();
//					}
//				}));
//		addFeature(new NumericFeature("minPauseNClick", 1, FeatureCategory.INTERACTION, false,
//				new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMinPauseNClick();
//					}
//				}));
//		addFeature(new NumericFeature("maxPauseNClick", 1, FeatureCategory.INTERACTION, false,
//				new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMaxPauseNClick();
//					}
//				}));
//		addFeature(new NumericFeature("meanPauseNClick", 1, FeatureCategory.INTERACTION, false,
//				new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMeanPauseNClick();
//					}
//				}));
//
//		addFeature(
//				new NumericFeature("minClickTime", 1, FeatureCategory.INTERACTION, false, new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//						if (statistic == null) {
//							return null;
//						}
//
//						return statistic.getMinClickTime();
//					}
//				}));
//		addFeature(
//				new NumericFeature("maxClickTime", 1, FeatureCategory.INTERACTION, false, new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMaxClickTime();
//					}
//				}));
//		addFeature(new NumericFeature("meanClickTime", 1, FeatureCategory.INTERACTION, false,
//				new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						MouseStatistic statistic = MouseStatisticFactory.get(user, NUMBER_OF_DIRECTIONS);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMeanClickTime();
//					}
//				}));		
//
//		
//
//		// Keyboard Features
//
//		addFeature(
//				new NumericFeature("minDwellTime", 1, FeatureCategory.INTERACTION, false, new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						KeyboardStatistic statistic = KeyboardStatisticFactory.get(user);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMaxDwellTime();
//					}
//				}));
//		addFeature(
//				new NumericFeature("maxDwellTime", 1, FeatureCategory.INTERACTION, false, new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						KeyboardStatistic statistic = KeyboardStatisticFactory.get(user);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMaxDwellTime();
//					}
//				}));
//		addFeature(new NumericFeature("meanDwellTime", 1, FeatureCategory.INTERACTION, false,
//				new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						KeyboardStatistic statistic = KeyboardStatisticFactory.get(user);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMeanDwellTime();
//					}
//				}));
//
//		addFeature(new NumericFeature("minFlightTime", 1, FeatureCategory.INTERACTION, false,
//				new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						KeyboardStatistic statistic = KeyboardStatisticFactory.get(user);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMaxFlightTime();
//					}
//				}));
//		addFeature(new NumericFeature("maxFlightTime", 1, FeatureCategory.INTERACTION, false,
//				new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						KeyboardStatistic statistic = KeyboardStatisticFactory.get(user);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMaxFlightTime();
//					}
//				}));
//		addFeature(new NumericFeature("meanFlightTime", 1, FeatureCategory.INTERACTION, false,
//				new Function<User, Double>() {
//
//					@Override
//					public Double apply(User user) {
//						if (user.getRegistrationLog() == null) {
//							return null;
//						}
//						KeyboardStatistic statistic = KeyboardStatisticFactory.get(user);
//						if (statistic == null) {
//							return null;
//						}
//						return statistic.getMeanFlightTime();
//					}
//				}));

	


	}

	/**
	 * Returns the instance of the FeatureFactory
	 * @return FeatureFactory
	 */
	public static FeatureFactory getInstance() {
		return FeatureFactoryHolder.INSTANCE;
	}

	/**
	 * Adds a feature to the feature factory
	 * @param feature feature to add
	 */
	public void addFeature(AbstractFeature feature) {
		features.add(feature);
	}

	/**
	 * Returns all features, corresponding to an certain category
	 * @param category category to which the returned features belong
	 * @return all features with the given category
	 */
	public List<AbstractFeature> getFeatures(FeatureCategory category) {
		List<AbstractFeature> featureList = new ArrayList<>();
		featureList.add(spammer.newInstance());
		for (AbstractFeature feature : features) {
			if (feature.getCategory() == category) {
				featureList.add(feature);
			}
		}
		return featureList;
	}

	/**
	 * Returns all features, corresponding to one of certain categories. 
	 * @param categories categories to which the returned features belong
	 * @return all features with one of given categories
	 */
	public List<String> getFeaturesFromCategoryList(List<FeatureCategory> categories) {
		List<String> featureList = new ArrayList<>();
		featureList.add(spammer.getName());
		for (AbstractFeature feature : features) {
			if (categories.contains(feature.getCategory())) {
				featureList.add(feature.getName());
			}
		}
		return featureList;
	}

	/**
	 * Returns all features by name
	 * @param featurenames names of the features
	 * @return features with one of the given names
	 */
	public List<AbstractFeature> getFeatures(List<String> featurenames) {
		List<AbstractFeature> featureList = new ArrayList<>();
		for (AbstractFeature feature : features) {
			if (featurenames.contains(feature.getName())) {

				AbstractFeature newInstance = feature.newInstance();
				if (newInstance == null) {
					System.out.println(feature.getName());
					System.exit(1);
				}
				featureList.add(newInstance);
			}
		}
		return featureList;
	}

	/**
	 * Return all features. 
	 * @return all features
	 */
	public List<AbstractFeature> getAllFeatures() {
		return new ArrayList<>(features);
	}

	/**
	 * Return all feature names as list
	 * @return feature names
	 */
	public List<String> getFeatureNames() {
		List<String> featureNames = new ArrayList<>();
		for (AbstractFeature feature : features) {
			featureNames.add(feature.getName());

		}
		return featureNames;
	}

	/**
	 * Makes a copy of every feature in the given list and return them in an new list
	 * @param features to copy
	 * @return copied features
	 */
	public List<AbstractFeature> deepCopy(List<AbstractFeature> toCopy) {
		List<AbstractFeature> featureList = new ArrayList<>();
		for (AbstractFeature feature : toCopy) {
			AbstractFeature newInstance = feature.newInstance();
			featureList.add(newInstance);
		}
		return featureList;
	}

}
