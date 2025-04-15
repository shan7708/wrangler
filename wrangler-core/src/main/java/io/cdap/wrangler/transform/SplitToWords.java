package io.cdap.wrangler.transform;

import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.RecipeException;
import io.cdap.wrangler.api.DirectiveContext;
import io.cdap.wrangler.api.annotation.Name;
import io.cdap.wrangler.api.annotation.Description;
import io.cdap.wrangler.api.annotation.Example;
import io.cdap.wrangler.api.annotation.Plugin;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.parser.UsageDefinitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Plugin(type = Directive.TYPE)
@Name("split-to-words")
@Description("Splits a column value into individual words")
@Example("split-to-words columnName")
public class SplitToWords implements Directive {

    private String column;

    @Override
    public UsageDefinition define() {
        UsageDefinitions definitions = UsageDefinitions.builder()
            .define("column", ColumnName.class)
            .build();
        return definitions;
    }

    @Override
    public void initialize(DirectiveContext context, Arguments arguments) throws RecipeException {
        column = ((ColumnName) arguments.value("column")).value();
    }

    @Override
    public List<Row> execute(List<Row> rows) throws RecipeException {
        List<Row> results = new ArrayList<>();

        for (Row row : rows) {
            int idx = row.find(column);
            if (idx != -1) {
                Object value = row.getValue(idx);
                if (value != null && value instanceof String) {
                    String[] words = ((String) value).trim().split("\\s+");
                    List<String> wordList = new ArrayList<>();
                    Collections.addAll(wordList, words);
                    row.setValue(idx, wordList);
                }
            }
            results.add(row);
        }
        return results;
    }
}
