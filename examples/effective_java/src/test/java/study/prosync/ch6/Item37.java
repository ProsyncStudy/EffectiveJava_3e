package study.prosync.ch6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.Test;

import lombok.extern.log4j.Log4j;
import study.prosync.ch6.Plant1.LifeCycle;

@Log4j
public class Item37 {
    // @Test
    // public void test1() {
    // // Using ordinal() to index into an array - DON'T DO THIS!
    // Set<Plant1>[] plantsByLifeCycle = (Set<Plant1>[]) new
    // Set[Plant1.LifeCycle.values().length];
    // for (int i = 0; i < plantsByLifeCycle.length; i++)
    // plantsByLifeCycle[i] = new HashSet<>();
    // for (Plant1 p : garden)
    // plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
    // // Print the results
    // for (int i = 0; i < plantsByLifeCycle.length; i++) {
    // System.out.printf("%s: %s%n",
    // Plant1.LifeCycle.values()[i], plantsByLifeCycle[i]);
    // }
    // }

    @Test
    public void test2() {
        // Using an EnumMap to associate data with an enum
        ArrayList<Plant1> garden = new ArrayList<>();

        garden.add(new Plant1("a1", LifeCycle.ANNUAL));
        garden.add(new Plant1("a2", LifeCycle.ANNUAL));
        garden.add(new Plant1("a3", LifeCycle.ANNUAL));
        garden.add(new Plant1("a4", LifeCycle.ANNUAL));

        // garden.add(new Plant1("b1", LifeCycle.PERENNIAL));
        // garden.add(new Plant1("b2", LifeCycle.PERENNIAL));
        // garden.add(new Plant1("b3", LifeCycle.PERENNIAL));
        // garden.add(new Plant1("b4", LifeCycle.PERENNIAL));

        garden.add(new Plant1("c1", LifeCycle.BIENNIAL));
        garden.add(new Plant1("c2", LifeCycle.BIENNIAL));
        garden.add(new Plant1("c3", LifeCycle.BIENNIAL));
        garden.add(new Plant1("c4", LifeCycle.BIENNIAL));

        Map<Plant1.LifeCycle, Set<Plant1>> plantsByLifeCycle = new EnumMap<>(Plant1.LifeCycle.class);
        for (Plant1.LifeCycle lc : Plant1.LifeCycle.values())
            plantsByLifeCycle.put(lc, new HashSet<>());
        for (Plant1 p : garden)
            plantsByLifeCycle.get(p.lifeCycle).add(p);
        log.info(plantsByLifeCycle);
    }

    @Test
    public void test3() {
        // Using an EnumMap to associate data with an enum
        ArrayList<Plant1> garden = new ArrayList<>();

        garden.add(new Plant1("a1", LifeCycle.ANNUAL));
        garden.add(new Plant1("a2", LifeCycle.ANNUAL));
        garden.add(new Plant1("a3", LifeCycle.ANNUAL));
        garden.add(new Plant1("a4", LifeCycle.ANNUAL));

        // garden.add(new Plant1("b1", LifeCycle.PERENNIAL));
        // garden.add(new Plant1("b2", LifeCycle.PERENNIAL));
        // garden.add(new Plant1("b3", LifeCycle.PERENNIAL));
        // garden.add(new Plant1("b4", LifeCycle.PERENNIAL));

        garden.add(new Plant1("c1", LifeCycle.BIENNIAL));
        garden.add(new Plant1("c2", LifeCycle.BIENNIAL));
        garden.add(new Plant1("c3", LifeCycle.BIENNIAL));
        garden.add(new Plant1("c4", LifeCycle.BIENNIAL));

        Function<Plant1, Plant1.LifeCycle> classfier = Plant1::getLifeCycle;
        // log.info(Arrays.stream(garden.toArray()).collect(Collectors.groupingBy(classfier)));
        // (Collectors.groupingBy(classfier)));
        // System.out.println(Arrays.stream(garden.toArray())
        // .collect(Collectors.groupingBy(classfier,
        // () -> new EnumMap<>(LifeCycle.class), Collectors.toSet())));

        // System.out.println(Arrays.stream(garden.toArray())
        // .collect(Collectors.groupingBy(classfier,
        // () -> new EnumMap<>(LifeCycle.class), Collectors.toSet())));

        EnumMap<Plant1.LifeCycle, ArrayList<Plant1>> enumMap = new EnumMap<>(Plant1.LifeCycle.class);
        for (Plant1 plant : garden) {
            Plant1.LifeCycle lifeCycle = plant.getLifeCycle();
            enumMap.computeIfAbsent(lifeCycle, k -> new ArrayList<>()).add(plant);
        }

        log.info(enumMap);

    }

    @Test
    public void test4() {
        log.info(Phase2.Transition.from(Phase2.SOLID, Phase2.LIQUID));
        log.info(Phase2.Transition.from(Phase2.LIQUID, Phase2.SOLID));
    }

    @Test
    public void test5() {
        log.info(Phase3.Transition.from(Phase3.SOLID, Phase3.LIQUID));
        log.info(Phase3.Transition.from(Phase3.LIQUID, Phase3.PLASMA));
    }

}
