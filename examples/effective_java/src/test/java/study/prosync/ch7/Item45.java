package study.prosync.ch7;

import autovalue.shaded.com.google.common.base.Supplier;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.util.stream.Collectors.groupingBy;

public class Item45 {
    private String alphabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }

    @Test
    public void 아나그램() throws Exception {
        int minGroupSize = 5;
        String pathname = null;
        File dictionary = new File(pathname);
        Map<String, Set<String>> groups = new HashMap<>();
        try (Scanner s = new Scanner(dictionary)) {
            while (s.hasNext()) {
                String word = s.next();
                groups.computeIfAbsent(alphabetize(word),
                        (unused) -> new TreeSet<>()).add(word);
            }
        }
        for (Set<String> group : groups.values()) {
            if (group.size() >= minGroupSize) {
                System.out.println(group.size() + ": " + group);
            }
        }
    }

    @Test
    public void 아나그램_가독성떨어짐() throws Exception {
        int minGroupSize = 5;
        String pathname = null;
        Path dictionary = Paths.get(pathname);
        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(
                            groupingBy(word -> word.chars().sorted()
                                    .collect(StringBuilder::new,
                                            (sb, c) -> sb.append((char) c),
                                            StringBuilder::append).toString()))
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .map(group -> group.size() + ": " + group)
                    .forEach(System.out::println);
        }
    }

    @Test
    public void 아나그램_가독성굳() throws Exception {
        int minGroupSize = 5;
        String pathname = null;
        Path dictionary = Paths.get(pathname);
        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(groupingBy(word -> alphabetize(word)))
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .forEach(group -> System.out.println(group.size() + ": " + group));
        }
    }

    @Test
    public void char는_스트림을_쓰지말자() throws Exception {
        // 잘못된 출력: 721011081081113211911111410810033 출력
        "Hello world!".chars().forEach(System.out::println);

        // 정상 출력
        "Hello world!".chars().forEach(x -> System.out.println((char) x));
    }

    @Test
    public void 메르센소수() throws Exception {
        BigInteger TWO = BigInteger.valueOf(2);
        Supplier<Stream<BigInteger>> priems = () -> Stream.iterate(TWO, BigInteger::nextProbablePrime);

        priems.get().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
                .filter(mersenne -> mersenne.isProbablePrime(50))
                .limit(20)
                .forEach(System.out::println);
    }

    @Test
    public void 메르센소수_지수까지() throws Exception {
        BigInteger TWO = BigInteger.valueOf(2);
        Supplier<Stream<BigInteger>> priems = () -> Stream.iterate(TWO, BigInteger::nextProbablePrime);

        priems.get().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
                .filter(mersenne -> mersenne.isProbablePrime(50))
                .limit(20)
                .forEach(System.out::println);
    }


}
