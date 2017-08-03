package thirdparty.de.slikey.effectlib.math;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;

public class SumTransform implements Transform {
	private Collection<Transform> inputs;

	@Override
	public void load(ConfigurationSection parameters) {
		inputs = Transforms.loadTransformList(parameters, "inputs");
	}

	@Override
	public double get(double t) {
		double value = 0;
		for (Transform transform : inputs) {
			value += transform.get(t);
		}
		return value;
	}
}
