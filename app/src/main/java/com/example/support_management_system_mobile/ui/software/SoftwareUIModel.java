package com.example.support_management_system_mobile.ui.software;

import com.example.support_management_system_mobile.models.Software;

import java.util.Objects;

public class SoftwareUIModel {
    private final Software software;
    private final boolean isExpanded;

    public SoftwareUIModel(Software software, boolean isExpanded) {
        this.software = software;
        this.isExpanded = isExpanded;
    }

    public Software getSoftware() {
        return software;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SoftwareUIModel that = (SoftwareUIModel) o;
        return isExpanded == that.isExpanded && software.equals(that.software);
    }

    @Override
    public int hashCode() {
        return Objects.hash(software, isExpanded);
    }
}
