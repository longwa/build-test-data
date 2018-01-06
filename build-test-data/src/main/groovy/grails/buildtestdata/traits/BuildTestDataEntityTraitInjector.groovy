package grails.buildtestdata.traits

import grails.compiler.traits.TraitInjector
import groovy.transform.CompileStatic
import org.grails.core.artefact.DomainClassArtefactHandler

@CompileStatic
class BuildTestDataEntityTraitInjector implements TraitInjector {

    @Override
    Class getTrait() {
        BuildTestDataEntity
    }

    @Override
    String[] getArtefactTypes() {
        [DomainClassArtefactHandler.TYPE] as String[]
    }
}
