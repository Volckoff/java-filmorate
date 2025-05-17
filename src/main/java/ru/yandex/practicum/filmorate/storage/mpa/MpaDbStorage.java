package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_MPA_BY_ID = "SELECT * FROM mpa_rating WHERE mpa_id = ?";
    private static final String FIND_ALL_MPA = "SELECT * FROM mpa_rating ORDER BY mpa_id";

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpaById(int id) {
        try {
            return jdbcTemplate.queryForObject(FIND_MPA_BY_ID, (rs, rn) -> {
                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("mpa_id"));
                mpa.setName(rs.getString("name"));
                return mpa;
            }, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("MPA с ID " + id + " не найден");
        }
    }


    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query(FIND_ALL_MPA, (rs, rn) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        });
    }


    public boolean existsById(Integer mpaId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM mpa_rating WHERE mpa_id = ?", Integer.class, mpaId);
        return count != null && count > 0;
    }
}
