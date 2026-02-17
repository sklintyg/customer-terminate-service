package se.inera.intyg.cts.infrastructure.service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import se.inera.intyg.cts.domain.service.PasswordGenerator;

@Component
public class RandomPasswordGenerator implements PasswordGenerator {

  public static final int NUMBER_COUNT = 2;
  public static final int SPECIAL_CHAR_COUNT = 2;
  public static final int UPPER_ALPHABET_COUNT = 2;
  public static final int LOWER_ALPHABET_COUNT = 4;

  public static final int NUMBER_ORIGIN = 48;
  public static final int NUMBER_BOUND = 57;
  public static final int SPECIAL_CHAR_ORIGIN = 33;
  public static final int SPECIAL_CHAR_BOUND = 47;
  public static final int UPPERCASE_ALPHABET_ORIGIN = 65;
  public static final int UPPERCASE_ALPHABET_BOUND = 90;
  public static final int LOWERCASE_ALPHABET_ORIGIN = 97;
  public static final int LOWERCASE_ALPHABET_BOUND = 122;

  Random random = new SecureRandom();

  @Override
  public String generateSecurePassword() {
    Stream<Character> pwdStream = Stream.concat(getRandomNumbers(NUMBER_COUNT),
        Stream.concat(getRandomSpecialChars(SPECIAL_CHAR_COUNT),
            Stream.concat(getRandomAlphabets(UPPER_ALPHABET_COUNT, true),
                getRandomAlphabets(LOWER_ALPHABET_COUNT, false))));
    List<Character> charList = pwdStream.collect(Collectors.toList());
    Collections.shuffle(charList);
    return charList.stream()
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
        .toString();
  }

  private Stream<Character> getRandomNumbers(int count) {
    IntStream numbers = random.ints(count, NUMBER_ORIGIN, NUMBER_BOUND);
    return numbers.mapToObj(data -> (char) data);
  }

  private Stream<Character> getRandomSpecialChars(int count) {
    IntStream specialChars = random.ints(count, SPECIAL_CHAR_ORIGIN, SPECIAL_CHAR_BOUND);
    return specialChars.mapToObj(data -> (char) data);
  }

  private Stream<Character> getRandomAlphabets(int count, boolean upperCase) {
    IntStream characters = null;
    if (upperCase) {
      characters = random.ints(count, UPPERCASE_ALPHABET_ORIGIN, UPPERCASE_ALPHABET_BOUND);
    } else {
      characters = random.ints(count, LOWERCASE_ALPHABET_ORIGIN, LOWERCASE_ALPHABET_BOUND);
    }
    return characters.mapToObj(data -> (char) data);
  }
}
