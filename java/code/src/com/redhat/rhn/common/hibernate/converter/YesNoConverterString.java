package com.redhat.rhn.common.hibernate.converter;

import org.hibernate.type.StandardBooleanConverter;
import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.java.StringJavaType;

public class YesNoConverterString  implements StandardBooleanConverter<String> {
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return toRelationalValue( attribute );
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return toDomainValue( dbData );
    }

    @Override
    public Boolean toDomainValue(String relationalForm) {
        if ( relationalForm == null ) {
            return null;
        }

        switch ( relationalForm ) {
            case "Y":
                return true;
            case "N":
                return false;
        }

        return null;
    }

    @Override
    public String toRelationalValue(Boolean domainForm) {
        if ( domainForm == null ) {
            return null;
        }

        return domainForm ? "Y" : "N";
    }

    @Override
    public JavaType<Boolean> getDomainJavaType() {
        return BooleanJavaType.INSTANCE;
    }

    @Override
    public JavaType<String> getRelationalJavaType() {
        return StringJavaType.INSTANCE;
    }

}
