package com.lxl.jrxy.mapper;

import com.lxl.jrxy.model.MyLog;
import com.lxl.jrxy.model.Tips;
import com.lxl.jrxy.model.scitc.ScitcUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserMapper {


//    /**
//     * 查询6点钟没有填报的信息表
//     * @return
//     */
//    @Select("select * from user right JOIN log on `user`.username = log.username and log.clock_six=0 and `day`=date(now())")
//    @Results(value = {
//            @Result(property = "myLog.id", column = "id"),
//            @Result(property = "myLog.username", column = "username"),
//            @Result(property = "myLog.day", column = "day"),
//            @Result(property = "myLog.clock_six", column = "clock_six"),
//            @Result(property = "myLog.clock_twelve", column = "clock_twelve"),
//            @Result(property = "myLog.six_describe", column = "six_describe"),
//            @Result(property = "myLog.twelve_describe", column = "twelve_describe")
//        }
//    )
//    public List<ScitcUser> findAll();


    /**
     * 查询所有的用户，并且用户已经开启自动签到
     * @return
     */
    @Select("select * from user where school='scitc' and auto=1")
    @Results(value = {
            @Result(property = "username",column = "username"),
            @Result(property = "myLog",column = "username", one = @One(select = "com.lxl.jrxy.mapper.UserMapper.findLog"))
    })
    public List<ScitcUser> findAll();
    @Select("select * from log where username=#{username} and day=date(now()) limit 0,1")
    public MyLog findLog();





















//    @Select("select * from log where username=#{username} and day=#{date}")
//    public MyLog findByUsernameAnddate(String username, Date date);


    /**
     * 查询一个用户的所有日志
     * @param username
     * @return
     */
    @Select("select * from log where username=#{username} ORDER BY `day` DESC LIMIT 20")
    public List<MyLog> findByUsernameAll(String username);
    /**
     * 根据用户名和密码查询
     * @param username
     * @param password
     * @return
     */
    @Select("select * from user where username=#{username} and password=#{password}")
    public ScitcUser findByUsernameAndPassword(String username,String password);


    /**
     * 添加新用户
     * @param scitcUser
     * @return
     */
    @Insert("insert into user(nick,username,password,address,temperature,email,dormitory,apartment,school,auto,userdepartment) values(#{nick},#{username},#{password},#{address},#{temperature},#{email},#{dormitory},#{apartment},#{school},#{auto},#{userdepartment})")
    public Integer addScitcUser(ScitcUser scitcUser);

    /**
     * 添加新日志
     * @param log
     * @return
     */
    @Insert("insert into log(username,day,clock_six,clock_twelve,six_describe,twelve_describe) values(#{username},#{day},#{clock_six},#{clock_twelve},#{six_describe},#{twelve_describe})")
    public Integer insertLog(MyLog log);

    /**
     * 日志更新
     * @param log
     * @return
     */
    @Update("update log set clock_six=#{clock_six},clock_twelve=#{clock_twelve},six_describe=#{six_describe},twelve_describe=#{twelve_describe} where username=#{username} and day=#{day}")
    public Integer updateLog(MyLog log);


    @Update("update user set password=#{password},address=#{address},temperature=#{temperature},email=#{email},dormitory=#{dormitory},apartment=#{apartment},auto=#{auto} where username=#{username}")
    public Integer updateUser(ScitcUser user);


    /**
     * 查询公告
     */
    @Select("select * from tips limit 0,1")
    public Tips getTips();


    /**
     * 获取所有的答案集合
     * @return
     */
    @Select("select * from question")
    public List<Map<String,String>> getAllQuestion();
}
