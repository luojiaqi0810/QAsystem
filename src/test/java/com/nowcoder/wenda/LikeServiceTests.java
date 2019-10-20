package com.nowcoder.wenda;

import com.nowcoder.wenda.service.LikeService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author LuoJiaQi
 * @Date 2019/10/20
 * @Time 17:19
 */

//加上junit和spring中测试相关的配置
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
public class LikeServiceTests {
    @Autowired
    LikeService likeService;

    // 测试前可能会有一些数据准备工作，初始化数据或表
    // 对所有使用@Test的方法有效！！！
    @Before
    public void setUp() {
        System.out.println("setUp");
    }

    // 测试后会有一些数据清除之类的操作
    // 比如在数据库中插入数据做测试用，测试完了之后需要清楚
    // 对所有使用@Test的方法有效！！！
    @After
    public void tearDown() {
        System.out.println("tearDown");
    }


    // 有些测试数据的初始化和清除只用做一次，不用每次测试都重新初始化和清楚，那么就可以用@BeforeClass和@AfterClass
    // class级别，所以不能写成成员方法，必须写成静态方法
    @BeforeClass
    public static void beforeClass() {
        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("afterClass");
    }



    @Test
    public void testLike() {
        System.out.println("testLike");
        likeService.like(123, 1, 1);
        Assert.assertEquals(1, likeService.getLikeStatus(123, 1, 1));

        likeService.disLike(123, 1, 1);
        Assert.assertEquals(-1, likeService.getLikeStatus(123, 1, 1));
    }


    @Test
    public void testXXX() {
        System.out.println("testXXX");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        System.out.println("testException");
        throw new IllegalArgumentException("发生异常");
    }
}