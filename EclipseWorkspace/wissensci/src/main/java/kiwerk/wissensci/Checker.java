package kiwerk.wissensci;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class Checker
{
    public static void main(String[] args) throws Exception {
	    Checker r = new Checker("knowledge.ttl");

	    if (r.isConsistent()) {
	        System.out.println("Ontology consistent");
	    } else {
	        throw new Exception("Ontology is not consistent");
	    }

	    r.checkIfAllClassesAreSatisfiable();
	}
	private OWLOntology ontology;
	private OWLReasoner reasoner;
  	private OWLDataFactory factory;

	private String namespace;

	public Checker(String ontoPath) throws OWLOntologyCreationException
	{
	    OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	    ontology = man.loadOntologyFromOntologyDocument(new File(ontoPath));
	    namespace = ontology.getOntologyID().getOntologyIRI().get().toString() + "#";
	    factory = man.getOWLDataFactory();
	    reasoner = new ReasonerFactory().createReasoner(ontology);
	    reasoner.precomputeInferences();
	}

    public void checkIfAllClassesAreSatisfiable() throws Exception
    {
        Set<OWLClass> classes = ontology.getClassesInSignature();

        System.out.println();
        System.out.println("Check classes if satisfiable:");

        OWLClass c;

        for (Iterator<OWLClass> ic = classes.iterator(); ic.hasNext();) {
            c = ic.next();
            System.out.println("");
            System.out.print(" - "+c.getIRI().toString().replace(namespace, "") +": ");

            if (reasoner.isSatisfiable(c)) {
                System.out.print("OK");
            } else {
                throw new Exception("Class "+ c.getIRI().toString() +" is not satisfiable!");
            }
        }
    }

    private OWLClass getCls(String name)
    {
        return factory.getOWLClass(namespace, name);
    }

    public boolean isConsistent()
	{
	    return reasoner.isConsistent();
	}

	public boolean isSatisfiable(String clsName)
    {
        return reasoner.isSatisfiable(getCls(clsName));
    }
}
