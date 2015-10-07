package omniapi.finders;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;

public class EntityFinder extends Finder<Entity> {

	public EntityFinder(Script script) {
		super(script);
	}

	@Override
	public Entity find(String name, FinderDistance distance) {
		return findThatMeetsCondition((target) -> (target != null && target.exists() && target.getName().equalsIgnoreCase(name)), distance);
	}

	@Override
	public Entity findThatMeetsCondition(String name, FinderCondition<Entity> condition, FinderDistance distance) {
		Stream<RS2Object> entities = getObjects().getAll().stream().filter(target -> (target != null && target.exists() && target.getName().equalsIgnoreCase(name) && condition.meetsCondition(target)));
		Optional<RS2Object> entity = ((distance.equals(FinderDistance.FURTHEST) || distance.equals(FinderDistance.FURTHEST_GAMETILES)) ? entities.max(getComparatorForDistance(distance)) : entities.min(getComparatorForDistance(distance)));
		
		return entity.orElse(null);
	}

	@Override
	public Entity findThatMeetsCondition(FinderCondition<Entity> condition, FinderDistance distance) {
		Stream<RS2Object> entities = getObjects().getAll().stream().filter(target -> (condition.meetsCondition(target)));
		Optional<RS2Object> entity = ((distance.equals(FinderDistance.FURTHEST) || distance.equals(FinderDistance.FURTHEST_GAMETILES)) ? entities.max(getComparatorForDistance(distance)) : entities.min(getComparatorForDistance(distance)));
		
		return entity.orElse(null);
	}

	@Override
	public Comparator<Entity> getComparatorForDistance(FinderDistance distance) {
		switch (distance) { //We handle FURTHEST/FURTHEST_GAMETILES in our find conditions, so we don't need to worry about what the comparators are
			default: case CLOSEST: case FURTHEST: return ((one, two) -> Integer.compare(getDistance(myPosition(), one.getPosition()), getDistance(myPosition(), two.getPosition())));
			case CLOSEST_GAMETILES: case FURTHEST_GAMETILES: return ((one, two) -> Integer.compare(getMap().distance(one.getPosition()), getMap().distance(two.getPosition())));
		}
	}

}
