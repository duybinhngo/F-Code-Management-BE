package fcode.backend.management.repository;

import fcode.backend.management.repository.entity.Resource;
import fcode.backend.management.repository.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {
    Resource findOneById(Integer resourceId);

    Resource findResourceByUrl(String url);

    @Query("select (count(r) > 0) from Resource r where r.subject.id = ?1")
    Boolean existsResourceBySubject(Integer subjectId);

    @Query(nativeQuery = true, value = "SELECT resource.id, resource.url, resource.contributor, resource.description, resource.subject_id FROM resource INNER JOIN subject ON resource.subject_id = subject.id ORDER BY subject.semester ASC")
    List<Resource> getAllResources();

    @Query("select r from Resource r where r.subject.id = ?1")
    List<Resource> getResourcesBySubjectId(Integer subjectId);

    @Query(nativeQuery = true, value = "SELECT resource.id, resource.url, resource.contributor, resource.description, resource.subject_id FROM resource INNER JOIN subject ON resource.subject_id = subject.id WHERE subject.semester = ?1 ORDER BY subject.name ASC ")
    List<Resource> getResourcesBySubjectSemester(Integer semester);

    boolean existsById(Integer id);

    @Query(nativeQuery = true, value = "SELECT * FROM resource WHERE LOWER(contributor) LIKE LOWER(?1)")
    List<Resource> searchResourcesByContributor(String contributor);
}
