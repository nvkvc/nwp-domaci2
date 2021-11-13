package framework.di.annotations;

public @interface Bean {
    String scope() default "prototype";
}
