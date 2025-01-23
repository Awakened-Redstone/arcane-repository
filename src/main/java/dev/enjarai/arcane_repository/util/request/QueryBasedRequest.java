package dev.enjarai.arcane_repository.util.request;

import dev.enjarai.arcane_repository.repository.request.Request;
import dev.enjarai.arcane_repository.util.FallbackUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.intellij.lang.annotations.Language;

import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public abstract class QueryBasedRequest extends Request {
    protected static final String QUESTION_MARK = "?";
    private static final List<QueryType> PATTERNS = List.of(
      new QueryType("(?<amount>\\d+ )?(shulkers?|chests?)( of|) (?<item>.+)", (amount, item) -> amount * item.getMaxCount() * 27),
      new QueryType("(?<amount>\\d+ |all )?stacks?( of|) (?<item>.+)", (amount, item) -> amount * item.getMaxCount()),
      new QueryType("(?<item>.+) (?<amount>\\d+ |all )?stacks?", (amount, item) -> amount * item.getMaxCount()),
      new QueryType("(?<amount>\\d+|all)x? (?<item>.+)", (amount, item) -> amount),
      new QueryType("(?<item>.+) (?<amount>\\d+|all)x?", (amount, item) -> amount),
      new QueryType("(?<item>.+)", (amount, item) -> 1)
    ); //TODO: rework this
    protected final String fullQuery;

    public QueryBasedRequest(int amount, String itemQuery) {
        super(amount);
        this.fullQuery = itemQuery;
    }

    public static QueryBasedRequest get(String query) {
        if (query.endsWith(QUESTION_MARK)) {
            var shortQuery = query.substring(0, query.length() - QUESTION_MARK.length());
            return new ListingRequest(Integer.MAX_VALUE, shortQuery);
        }

        for (var info : PATTERNS) {
            var matcher = info.pattern().matcher(query);
            if (matcher.matches()) {
                var amount = FallbackUtils.matcherGroupOrFallback(matcher, "amount", "1");
                var item = matcher.group("item");
                var amountInt = amount.equalsIgnoreCase("all") ? Integer.MAX_VALUE / 64 : Integer.parseInt(amount);
                return new ExtractionRequest(item, amountInt, info.amountModifier());
            }
        }

        return new ExtractionRequest(query, 1, (amount, item) -> 1);
    }

    protected static boolean matchGlob(String[] expression, String string) {
        if (expression.length == 1) {
            return expression[0].equals(string);
        }

        if (!string.startsWith(expression[0])) {
            return false;
        }

        int offset = expression[0].length();
        for (int i = 1; i < expression.length - 1; i++) {
            String section = expression[i];
            int found = string.indexOf(section, offset);
            if (found == -1) return false;
            offset = found + section.length();
        }
        return string.substring(offset).endsWith(expression[expression.length - 1]);
    }

    protected static boolean itemMatchesExpression(String[] expression, Item item) {
        String itemName = item.toString().toLowerCase(Locale.ROOT).trim();
        String itemCustomName = item.getName().getString().toLowerCase(Locale.ROOT).trim();

        return matchMultiplesBlob(expression, itemName)
               || matchMultiplesBlob(expression, itemCustomName);
    }

    protected static boolean matchMultiplesBlob(String[] expression, String query) {
        return matchGlob(expression, query)
               || matchGlob(expression, query + "s")
               || matchGlob(expression, query + "es")
               || query.endsWith("y") && matchGlob(expression, query.substring(0, query.length() - 1) + "ies");
    }

    public String getFullQuery() {
        return fullQuery;
    }

    public List<ItemStack> getReturnedStacks() {
        return List.of();
    }

    public abstract Text getMessage();

    private record QueryType(Pattern pattern, BiFunction<Integer, Item, Integer> amountModifier) {
        public QueryType(@Language("regexp") String regex, BiFunction<Integer, Item, Integer> amountModifier) {
            this(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), amountModifier);
        }
    }
}
