package org.dynjs.parser.statement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.qmx.jitescript.CodeBlock;

import org.antlr.runtime.tree.Tree;
import org.dynjs.parser.ParserException;
import org.dynjs.parser.Statement;
import org.dynjs.runtime.DynRegExp;

public class RegularExpressionStatement extends BaseStatement implements
		Statement {

	// TODO: Move the parsing logic to the parser
	static class DynRegExpParser {
		private static final String REG_EXP_PATTERN = "^\\/(.*)\\/([igm]{0,})$";

		static DynRegExp parse(String text) {
			Pattern pattern = Pattern.compile(REG_EXP_PATTERN);
			Matcher matcher = pattern.matcher(text);
			if (matcher.matches()) {
				return new DynRegExp(matcher.group(1),
						parseFlags(matcher.group(2)),
						parseGlobalMatchFlag(matcher.group(2)));
			}

			return null;
		}

		private static int parseFlags(String flags) {
			int result = 0;
			for (char flag : flags.toCharArray()) {
				result |= patternFlag(flag);
			}

			return result;
		}

		private static int patternFlag(char c) {
			if (c == 'i') {
				return Pattern.CASE_INSENSITIVE;
			}
			if (c == 'm') {
				return Pattern.MULTILINE;
			}

			return 0;
		}

		private static boolean parseGlobalMatchFlag(String flags) {
			return flags.contains("g");
		}
	}

	private final DynRegExp regExp;

	public RegularExpressionStatement(Tree tree) {
		super(tree);
		this.regExp = DynRegExpParser.parse(tree.getText());
		if (regExp == null) {
			throw new ParserException("Invalid regular expression", tree);
		}
	}

	@Override
	public CodeBlock getCodeBlock() {
		return null;
	}
}
