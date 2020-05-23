package com.newgen.am.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, FormatGroup.class, LengthGroup.class, UniqueGroup.class})
public interface ValidationSequence {

}
