package com.jaxio.celerio.configuration.entity;

import lombok.Data;
import repackaged.org.hibernate.search.annotations.Analyze;

@Data
public class FullTextField {
    public String name;
    public Analyze analyze;
    public Boolean projectable;
}
