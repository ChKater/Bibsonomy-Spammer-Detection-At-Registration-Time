package de.luh.chkater.spammerdetection.feature.interfaces;

import java.util.Arrays;
import java.util.function.Function;

import org.bibsonomy.model.User;

import weka.core.Attribute;

/**
 * TODO: add documentation to this class
 *
 * @author kater
 */
public class BooleanFeature extends NominalFeature{
	
	private Function<User, Boolean> booleanTransformation;


	/**
	 * @param name name of the feature
	 * @param version version of the feature
	 * @param category category of the feature
	 * @param needAllUser do the feature needs all users for precalculation
	 * @param booleanTransformation transforms a user to true (1) or false (0) value
	 */
	public BooleanFeature(String name, int version, FeatureCategory category, boolean needAllUser, Function<User, Boolean> booleanTransformation) {
		super(name, version, category, needAllUser, Arrays.asList("0", "1"), null);
		this.booleanTransformation = booleanTransformation;
		super.transformation = new Function<User, String>() {

			@Override
			public String apply(User user) {
				Boolean bool = BooleanFeature.this.booleanTransformation.apply(user);
				if(bool == null){
					return null;
				}
				return bool ? "1" : "0";
			}
		};
		
	}
	
	@Override
	public AbstractFeature newInstance() {
		// TODO Auto-generated method stub
		return new BooleanFeature(getName(), getVersion(), getCategory(), isNeedAllUser(), booleanTransformation);
	}
	
	
	


}
