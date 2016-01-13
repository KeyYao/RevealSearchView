package moe.key.yao.search.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Key on 2015/12/15.
 *  <p>
 *      NoProguard, Means not proguard something, like class, method, field
 *  </p>
 *  <p>
 *      参考<a href="http://www.trinea.cn/android/android-proguard-tip-not-proguard">Proguard部分类不混淆技巧</a>
 *  </p>
 * @author Key
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface NotProguard {

}
