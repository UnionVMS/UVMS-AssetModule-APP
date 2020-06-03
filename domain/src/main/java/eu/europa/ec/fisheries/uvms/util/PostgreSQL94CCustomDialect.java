package eu.europa.ec.fisheries.uvms.util;

import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

/**
 * This custom dialect extends the standard PostgreSQL dialect by introducing the
 * standard {@code REPLACE} SQL function as the {@code REPLACE_} JPQL function.
 * <p>
 * As of this writing, this is only needed in the {@code DynamicQueryGeneratorBean}.
 * <p>
 * The problem is that we need to replace the comma in number values from the database
 * with a period and cast them to number. The standard SQL {@code REPLACE} function
 * is not available in JPQL, so we have to use {@code CAST(FUNCTION('replace', x, ',', '.'))}.
 * See <a href="https://hibernate.atlassian.net/browse/HHH-2569">this open bug</a>
 * why this does not work
 * and <a href="https://hibernate.atlassian.net/browse/HHH-11938">this open bug</a>
 * that suggests going for this solution.
 */
public class PostgreSQL94CCustomDialect extends PostgreSQL94Dialect {
	public PostgreSQL94CCustomDialect() {
		registerFunction("REPLACE_", new StandardSQLFunction("REPLACE", StandardBasicTypes.STRING));
	}
}
