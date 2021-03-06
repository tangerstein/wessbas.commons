/***************************************************************************
 * Copyright (c) 2016 the WESSBAS project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/


package wessbas.commons.util;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.QueryDelegate;
import org.eclipse.ocl.examples.pivot.delegate.OCLDelegateDomain;
import org.eclipse.ocl.examples.pivot.delegate.OCLInvocationDelegateFactory;
import org.eclipse.ocl.examples.pivot.delegate.OCLQueryDelegateFactory;
import org.eclipse.ocl.examples.pivot.delegate.OCLSettingDelegateFactory;
import org.eclipse.ocl.examples.pivot.delegate.OCLValidationDelegateFactory;
import org.eclipse.ocl.examples.pivot.model.OCLstdlib;
import org.eclipse.ocl.examples.xtext.completeocl.CompleteOCLStandaloneSetup;
import org.eclipse.ocl.examples.xtext.essentialocl.EssentialOCLStandaloneSetup;
import org.eclipse.ocl.examples.xtext.oclinecore.OCLinEcoreStandaloneSetup;
import org.eclipse.ocl.examples.xtext.oclstdlib.OCLstdlibStandaloneSetup;

/**
 * Stand-alone validator for Ecore objects, to be used for validating EMF
 * features (e.g., missing children) and OCL constraints as well.
 *
 * @author   Eike Schulz (esc@informatik.uni-kiel.de)
 * @version  1.0
 */
public class EcoreObjectValidator {

    /** Tabulator to be used for line indent. */
    private final static String TAB = "    ";
    
    private final static String INFO_MODEL_VALIDATION_STARTED =
            "Validating M4J-DSL model ...";
    
    private final static String INFO_MODEL_VALIDATION_SUCCESSFUL =
            "Validation of M4J-DSL model successful.";

    private final static String ERROR_MODEL_VALIDATION_FAILED =
            "Validation of M4J-DSL model failed.";


    /* ***************************  constructors  *************************** */


    /**
     * Constructor for an Ecore Object Validator.
     */
    public EcoreObjectValidator () {

        this.init();
    }


    /* **************************  public methods  ************************** */


    /**
     * Validates a given Ecore object and prints the diagnostic results.
     *
     * @param eObject  Ecore object to be validated.
     *
     * @return  <code>true</code> if and only if the validation was successful.
     */
    public boolean validateAndPrintResult (final EObject eObject) {
    	
        System.out.println(INFO_MODEL_VALIDATION_STARTED);

        final Diagnostic diagnostic = this.validate(eObject);

        final boolean success = (diagnostic.getSeverity() == Diagnostic.OK);

        if (!success) {

            printDiagnostic(diagnostic);
            
            System.out.println(ERROR_MODEL_VALIDATION_FAILED);
            
        } else {
        	
        	System.out.println(INFO_MODEL_VALIDATION_SUCCESSFUL);
        	
        }       

        return success;
    }

    /**
     * Validates a given Ecore object.
     *
     * @param eObject  Ecore object to be validated.
     *
     * @return  the diagnostic results.
     */
    public Diagnostic validate (final EObject eObject) {

        return Diagnostician.INSTANCE.validate(eObject);
    }

    /* **************************  private methods  ************************* */


    /**
     * Prints a given diagnostic result.
     *
     * @param diagnostic  the diagnostic result to be printed.
     */
    private void printDiagnostic (final Diagnostic diagnostic) {

        this.printDiagnostic(diagnostic, "");
    }

    /**
     * Prints a given diagnostic result instance with a leading indent.
     *
     * @param diagnostic  the diagnostic result to be printed.
     * @param indent      indent to be inserted.
     */
    private void printDiagnostic (
            final Diagnostic diagnostic,
            final String indent) {

        System.out.println(indent + diagnostic.getMessage());

        for (final Diagnostic child : diagnostic.getChildren()) {

                this.printDiagnostic(child, indent + EcoreObjectValidator.TAB);
        }
    }

    /**
     * Initializes the stand-alone OCL validation.
     */
    private void init () {

        OCLstdlib.install();
        OCLstdlibStandaloneSetup.doSetup();
        OCLinEcoreStandaloneSetup.doSetup();
        CompleteOCLStandaloneSetup.doSetup();
        EssentialOCLStandaloneSetup.doSetup();

        String oclDelegateURI = OCLDelegateDomain.OCL_DELEGATE_URI_PIVOT;

        EOperation.Internal.InvocationDelegate.Factory.Registry.INSTANCE.put(
                oclDelegateURI, new OCLInvocationDelegateFactory.Global());

        EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE.put(
                oclDelegateURI, new OCLSettingDelegateFactory.Global());

        EValidator.ValidationDelegate.Registry.INSTANCE.put(
                oclDelegateURI, new OCLValidationDelegateFactory.Global());

        QueryDelegate.Factory.Registry.INSTANCE.put(
                oclDelegateURI, new OCLQueryDelegateFactory.Global());
    }
}
