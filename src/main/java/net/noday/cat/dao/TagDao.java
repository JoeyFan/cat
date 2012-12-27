/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.noday.cat.dao;

import java.util.List;

import net.noday.cat.model.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * cat TagDao
 *
 * @author <a href="http://www.noday.net">Noday</a>
 * @version , 2012-12-25
 * @since 
 */
@Repository
public class TagDao {

	@Autowired private JdbcTemplate jdbc;
	@Autowired private NamedParameterJdbcTemplate namedJdbc;
	
	public List<Tag> findAll() {
		String sql = "select * from tag order by ref_count desc";
		List<Tag> list = jdbc.query(sql, new BeanPropertyRowMapper<Tag>(Tag.class));
		return list;
	}
	
	public List<Tag> findAllTag() {
		String sql = "select * from tag order by ref_count desc";
		List<Tag> list = jdbc.queryForList(sql, Tag.class);
		return list;
	}
	
	public void updateTagRefCount(String tagName) {
		String sql = "update tag set ref_count=ref_count+1 where name=?";
		jdbc.update(sql, tagName);
	}
	
	public void updateTagRef(long aid, String tagName) {
		String csql = "select count(a.id) from ref_count a,tag b where a.tag_id=b.id and a.target_id=? and b.name=?";
		int count = jdbc.queryForInt(csql, aid, tagName);
		if (count == 0) {
			
		}
		String sql = "update tag set ref_count=ref_count+1 where name=?";
		jdbc.update(sql, tagName);
	}
	
	public long save(String tagName) {
		String sql = "insert into tag(name) values(:name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(sql, new BeanPropertySqlParameterSource(new Tag(tagName)), keyHolder);
        return keyHolder.getKey().longValue();
	}
	
	public void saveRef(long aid, long tagId, int type) {
		String sql = "insert into tag_ref(tag_id,target_id) values(?,?)";
		jdbc.update(sql, tagId, aid);
	}
	
	public Tag saveTagAndRef(long aid, String tagName) {
		long tid = save(tagName);
		saveRef(aid, tid, 1);
		return new Tag(tid, tagName, 1);
	}
}
