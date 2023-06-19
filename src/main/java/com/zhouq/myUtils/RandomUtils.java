package com.zhouq.myUtils;

import java.util.Random;

/**
 * <p>
 *
 * </p>
 *
 * @author 计算机系 周启俊
 * @since 2023/6/19 18:36
 */

public class RandomUtils {
    public static int randomInt(int length){
        Random random = new Random();
        return random.nextInt(1000, 9999);
    }
}
