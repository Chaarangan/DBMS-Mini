/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class PersistentTransactionDAO implements TransactionDAO {
    SQLiteOpenHelper sqLiteOpenHelper;

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount){
        ContentValues values = new ContentValues();
        values.put("date", String.valueOf(date));
        values.put("accountNo", accountNo);
        values.put("expenseType", String.valueOf(expenseType));
        values.put("amount", amount);

        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();

        db.insert("transactions", null, values);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        String query = "Select * FROM transactions";

        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Transaction> transactions = new ArrayList<Transaction>();


        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE);
                Date date = df.parse(cursor.getString(1));
                String accountNo = cursor.getString(2);
                ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(3));
                double amount = cursor.getDouble(4);
                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                transactions.add(transaction);
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            transactions = null;
        }
        db.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        String query = "Select * FROM transactions LIMIT " + limit + "\"";

        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Transaction> transactions = new ArrayList<Transaction>();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
                Date date = df.parse(cursor.getString(1));
                String accountNo = cursor.getString(2);
                ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(3));
                double amount = cursor.getDouble(4);
                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                transactions.add(transaction);
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            transactions = null;
        }
        db.close();
        return transactions;
    }
}
