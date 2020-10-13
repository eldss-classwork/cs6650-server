package data.models;

//
// MIGHT BE ABLE TO DELETE THIS CLASS
//

import java.util.Objects;

public class Resort implements JSONable {

  private String resortId;

  public Resort(String resortId) {
    this.resortId = resortId;
  }

  public String getResortId() {
    return resortId;
  }

  public void setResortId(String resortId) {
    this.resortId = resortId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Resort resort = (Resort) o;
    return Objects.equals(resortId, resort.resortId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resortId);
  }

  @Override
  public String toString() {
    return "Resort{" +
        "resortId='" + resortId + '\'' +
        '}';
  }

  @Override
  public String fieldsToJSON() {
    // Not resortID due to API specs
    return "{\"resortName\": \"" + resortId + "\"}";
  }
}
