package com.javadiscord.javabot.economy.dao;

import com.javadiscord.javabot.economy.model.Account;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class AccountRepository {
	private final Connection con;

	public void saveNewAccount(Account account) throws SQLException {
		PreparedStatement stmt = this.con.prepareStatement("INSERT INTO economy_account (user_id, balance) VALUES (?, ?)");
		stmt.setLong(1, account.getUserId());
		stmt.setLong(2, account.getBalance());
		stmt.executeUpdate();
		stmt.close();
	}

	public void updateAccount(Account account) throws SQLException {
		PreparedStatement stmt = this.con.prepareStatement("UPDATE economy_account SET balance = ? WHERE user_id = ?");
		stmt.setLong(1, account.getBalance());
		stmt.setLong(2, account.getUserId());
		stmt.executeUpdate();
		stmt.close();
	}

	public Account getAccount(long userId) throws SQLException {
		try (var stmt = this.con.prepareStatement("SELECT * FROM economy_account WHERE user_id = ?")) {
			stmt.setLong(1, userId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return this.readAccount(rs);
			}
			return null;
		}
	}

	private Account readAccount(ResultSet rs) throws SQLException {
		Account account = new Account();
		account.setUserId(rs.getLong("user_id"));
		account.setBalance(rs.getLong("balance"));
		return account;
	}
}
