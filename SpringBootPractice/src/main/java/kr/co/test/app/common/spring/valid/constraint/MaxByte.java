package kr.co.test.app.common.spring.valid.constraint;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxByteValidator.class)
public @interface MaxByte {

	String message() default "{MaxByte.message}";
	
	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
	
	int max();

	String charsetName() default "";
}
