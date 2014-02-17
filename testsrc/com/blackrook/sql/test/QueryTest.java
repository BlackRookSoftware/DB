package com.blackrook.sql.test;

import com.blackrook.commons.Common;
import com.blackrook.db.Query;
import com.blackrook.db.QueryOperation;
import com.blackrook.db.SelectQuery;

import static com.blackrook.db.SelectQuery.*;

public final class QueryTest
{

	public static void main(String[] args)
	{
		SelectQuery q = Query.select()
				.from(table("Users", "u"))
				.where(criterion("name", QueryOperation.LIKE, "M%"))
				.orderBy(sort("lastName")).limit(20, 0);
		Common.noop();
}

}
