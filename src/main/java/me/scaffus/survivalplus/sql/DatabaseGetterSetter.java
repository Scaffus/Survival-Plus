package me.scaffus.survivalplus.sql;

import me.scaffus.survivalplus.Helper;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseGetterSetter {
    private Connection connection;
    private Helper helper = new Helper();

    public DatabaseGetterSetter(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement ps(String statement) {
        PreparedStatement pS = null;
        try {
            pS = connection.prepareStatement(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pS;
    }

    public void createPlayer(Player p) {
        try {
            UUID uuid = p.getUniqueId();

            if (!playerExists(uuid)) {
                PreparedStatement psBank = ps("INSERT IGNORE INTO players_bank (UUID, BALANCE) VALUES (?, ?)");
                psBank.setString(1, uuid.toString());
                psBank.setInt(2, 0);
                psBank.executeUpdate();

                PreparedStatement psSkills = ps("INSERT IGNORE INTO players_skills (UUID, FARMING, MINING, COMBAT, RUNNING, DEATH, ARCHERY, SWIMMING, FLYING) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                psSkills.setString(1, uuid.toString());
                psSkills.setInt(2, 0);
                psSkills.setInt(3, 0);
                psSkills.setInt(4, 0);
                psSkills.setInt(5, 0);
                psSkills.setInt(6, 0);
                psSkills.setInt(7, 0);
                psSkills.setInt(8, 0);
                psSkills.setInt(9, 0);
                psSkills.executeUpdate();

                PreparedStatement psLevels = ps("INSERT IGNORE INTO players_levels (UUID, FARMING, MINING, COMBAT, RUNNING, DEATH, ARCHERY, SWIMMING, FLYING) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                psLevels.setString(1, uuid.toString());
                psLevels.setInt(2, 0);
                psLevels.setInt(3, 0);
                psLevels.setInt(4, 0);
                psLevels.setInt(5, 0);
                psLevels.setInt(6, 0);
                psLevels.setInt(7, 0);
                psLevels.setInt(8, 0);
                psLevels.setInt(9, 0);
                psLevels.executeUpdate();

                PreparedStatement psData = ps("INSERT IGNORE INTO players_data (UUID, TOKENS) VALUES (?, ?)");
                psData.setString(1, uuid.toString());
                psData.setInt(2, 0);
                psData.executeUpdate();

                PreparedStatement psPlayers = ps("INSERT IGNORE INTO players (UUID, NAME) VALUES (?, ?)");
                psPlayers.setString(1, uuid.toString());
                psPlayers.setString(2, p.getDisplayName());
                psPlayers.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean playerExists(UUID uuid) {
        try {
            PreparedStatement pS = ps("SELECT * FROM players WHERE UUID=?");
            pS.setString(1, uuid.toString());
            ResultSet result = pS.executeQuery();
            return result.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPlayerBalance(UUID uuid) {
        try {
            PreparedStatement pS = ps("SELECT * FROM players_bank WHERE UUID=?");
            pS.setString(1, uuid.toString());
            ResultSet result = pS.executeQuery();
            if (result.next()) {
                return result.getInt("balance");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public boolean addPlayerBalance(UUID uuid, int amount) {
        try {
            PreparedStatement pS = ps("UPDATE players_bank SET balance = balance + ? WHERE UUID=?");
            pS.setInt(1, amount);
            pS.setString(2, uuid.toString());
            pS.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPlayerBalance(UUID uuid, int amount) {
        try {
            PreparedStatement pS = ps("UPDATE players_bank SET balance=? WHERE UUID=?");
            pS.setInt(1, amount);
            pS.setString(2, uuid.toString());
            pS.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void incrementPlayerSkillPoints(UUID uuid, String skill, Double amount) {
        try {
            PreparedStatement pS = ps("UPDATE players_skills SET " + skill + " = " + skill + " + ? WHERE UUID=?");
            pS.setDouble(1, amount);
            pS.setString(2, uuid.toString());
            pS.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Double getPlayerSkillPoints(UUID uuid, String skill) {
        try {
            PreparedStatement pS = ps("SELECT " + skill + " FROM players_skills WHERE UUID=?");
            pS.setString(1, uuid.toString());
            ResultSet result = pS.executeQuery();
            if (result.next()) {
                return helper.round(result.getDouble(skill), 2);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    public void incrementPlayerTokens(UUID uuid, Integer amount) {
        try {
            PreparedStatement pS = ps("UPDATE players_data SET tokens = tokens + ? WHERE UUID=?");
            pS.setInt(1, amount);
            pS.setString(2, uuid.toString());
            pS.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void incrementPlayerSkillLevel(UUID uuid, String skill, Integer amount) {
        try {
            PreparedStatement pS = ps("UPDATE players_levels SET " + skill + " = " + skill + " + ? WHERE UUID=?");
            pS.setInt(1, amount);
            pS.setString(2, uuid.toString());
            pS.executeUpdate();

            incrementPlayerTokens(uuid, 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getPlayerSkillLevel(UUID uuid, Object skill) {
        try {
            PreparedStatement pS = ps("SELECT " + skill + " FROM players_levels WHERE UUID=?");
            pS.setString(1, uuid.toString());
            ResultSet result = pS.executeQuery();
            if (result.next()) {
                return result.getInt(skill);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public Integer getPlayerTokens(UUID uuid) {
        try {
            PreparedStatement pS = ps("SELECT tokens FROM players_data WHERE UUID=?");
            pS.setString(1, uuid.toString());
            ResultSet result = pS.executeQuery();
            if (result.next()) {
                return result.getInt("tokens");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public void setPlayerUpgrade(UUID uuid, String upgradeName, Boolean status) {
        try {
            PreparedStatement pS = ps("UPDATE players_upgrades SET ? = ? WHERE UUID=?");
            pS.setString(1, upgradeName);
            pS.setBoolean(2, status);
            pS.setString(3, uuid.toString());
            pS.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean getPlayerUpgrade(UUID uuid, String upgradeName) {
        try {
            PreparedStatement pS = ps("SELECT ? FROM players_upgrades WHERE UUID=?");
            pS.setString(1, upgradeName);
            pS.setString(2, uuid.toString());
            ResultSet result = pS.executeQuery();
            if (result.next()) {
                return result.getBoolean(upgradeName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
