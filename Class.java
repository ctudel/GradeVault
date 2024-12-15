/**
 * Class
 */
public class Class {
  private final int classId;
  private final String course;
  private final String term;
  private final int section;
  private final String description;

  public Class() {
    this.classId = -1;
    this.course = "";
    this.term = "";
    this.section = -1;
    this.description = "";
  }

  public Class(final int classId, final String course, final String term, final int section, final String description) {
    this.classId = classId;
    this.course = course;
    this.term = term;
    this.section = section;
    this.description = description;
  }

  public int getClassId() {
    return classId;
  }

  public String getCourse() {
    return course;
  }

  public String getTerm() {
    return term;
  }

  public int getSection() {
    return section;
  }

  public String getDescription() {
    return description;
  }

}
