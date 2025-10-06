package net.ausiasmarch.capitals.service;

import java.util.List;
import java.util.Objects;

import net.ausiasmarch.capitals.dao.ScoreDao;
import net.ausiasmarch.capitals.model.ScoreDto;
import net.ausiasmarch.shared.connection.HikariPool;

import java.sql.*;
import java.time.LocalDateTime;

public class ScoreService {

    public boolean set(int userId, boolean correct) throws SQLException {
        
        try (Connection oConnection = HikariPool.getConnection()) {

            ScoreDao oScoreDao = new ScoreDao(oConnection);
            if (oScoreDao.count(userId) > 1) {
                oScoreDao.sanitize();
            }
            ScoreDto oUserScore = oScoreDao.get(userId);
            if (!Objects.isNull(oUserScore)) {
                oUserScore.setTries(oUserScore.getTries() + 1);
                if (correct) {
                    oUserScore.setScore(oUserScore.getScore() + 1);
                }
                return oScoreDao.update(oUserScore) > 0;
            } else {
                oUserScore = new ScoreDto();
                oUserScore.setUserId(userId);
                oUserScore.setTries(1);
                if (correct) {
                    oUserScore.setScore(1);
                } else {
                    oUserScore.setScore(0);
                }
                oUserScore.setTimestamp(LocalDateTime.now());
                return oScoreDao.insert(oUserScore) > 0;
            }
        }

    }

    public List<ScoreDto> getHighScores() throws SQLException {        
        try (Connection oConnection = HikariPool.getConnection()) {
            ScoreDao oScoreDao = new ScoreDao(oConnection);
            return oScoreDao.getTop10();
        }
    }

}
