package com.ist.curriculum.opendataparser.web
import org.edtech.curriculum.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


data class NameAndCode(val name: String,val code: String)

@Controller
class WebController(@Autowired
                    private val curriculumService: CurriculumService) {

    val schoolFormsPerOperations: Map<String, List<SchoolType>> = mapOf(
        "GR" to listOf(SchoolType.GR, SchoolType.GRS, SchoolType.GRSAM, SchoolType.GRSPEC, SchoolType.GRSSPEC, SchoolType.SPEC),
        "GY" to listOf(SchoolType.GY, SchoolType.GYS),
        "VUX" to listOf(SchoolType.VUXGR, SchoolType.VUXGRS, SchoolType.SFI))

    val subjectNamesPerSchoolForm: Map<SchoolType, List<NameAndCode>> = SchoolType.values()
            .map {
                Pair(it, curriculumService.getSubjects(it)
                        .map { NameAndCode(it.name, it.code) }
                        .sortedBy { it.name }
                        .toList())
            }.toMap()

    @GetMapping("/")
    fun courseList(model: MutableMap<String, Any>): String {
        model["syllabuses"] = subjectNamesPerSchoolForm
        model["gropedSchoolForms"] = schoolFormsPerOperations
        model["schoolType"] = SchoolType.GR
        return "subject_list"
    }

    @GetMapping("/subject/{schoolType}")
    fun subjects(@PathVariable schoolType: SchoolType, model: MutableMap<String, Any>): String {
        // Parse Subject XMl Structure
        model["syllabuses"] = subjectNamesPerSchoolForm
        model["gropedSchoolForms"] = schoolFormsPerOperations
        model["schoolType"] = schoolType

        return "subject_list"
    }

    @GetMapping("/subject/{schoolType}/{subjectCode}")
    fun subject(@PathVariable schoolType: SchoolType, @PathVariable subjectCode: String, model: MutableMap<String, Any>): String {
        // Parse Subject XMl Structure
        model["subject"] = curriculumService.getSubject(schoolType, subjectCode)
        model["schoolType"] = schoolType
        model["syllabuses"] = subjectNamesPerSchoolForm
        model["gropedSchoolForms"] = schoolFormsPerOperations
        return "subject"
    }

}