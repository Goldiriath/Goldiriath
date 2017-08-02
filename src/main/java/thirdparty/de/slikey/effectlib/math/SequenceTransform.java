package thirdparty.de.slikey.effectlib.math;

import thirdparty.de.slikey.effectlib.util.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SequenceTransform implements Transform {
	private List<Sequence> steps;

	private class Sequence {
		private final Transform transform;
		private final double start;

		public Sequence(ConfigurationSection configuration) {
			this.transform = Transforms.loadTransform(configuration, "transform");
			this.start = configuration.getDouble("start", 0);
		}

		public double getStart() {
			return start;
		}

		public double get(double t) {
			return transform.get(t);
		}
	};

	@Override
	public void load(ConfigurationSection parameters) {
		steps = new ArrayList<Sequence>();
		Collection<ConfigurationSection> stepConfigurations = ConfigUtils.getNodeList(parameters, "steps");
		if (stepConfigurations != null) {
			for (ConfigurationSection stepConfig : stepConfigurations) {
				steps.add(new Sequence(stepConfig));
			}
		}
		Collections.reverse(steps);
	}

	@Override
	public double get(double t) {
		double value = 0;
		for (Sequence step : steps) {
			if (step.getStart() <= t) {
				return step.get(t);
			}
		}
		return value;
	}
}
