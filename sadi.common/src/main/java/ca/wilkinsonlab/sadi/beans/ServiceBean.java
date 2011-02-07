package ca.wilkinsonlab.sadi.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ca.wilkinsonlab.sadi.ServiceDescription;

/**
 * A simple class describing a SADI service.
 * @author Luke McCarthy
 */
public class ServiceBean implements Serializable, ServiceDescription
{
	private static final long serialVersionUID = 1L;
	
	private String URI;
	private String name;
	private String description;
	private String provider;
	private String email;
	private boolean authoritative;
	private String inputClassURI;
	private String inputClassLabel;
	private String outputClassURI;
	private String outputClassLabel;
	private Collection<RestrictionBean> restrictions;
	private String parameterClassURI;
	private String parameterClassLabel;
//	private String parameterInstanceURI;
	
	public ServiceBean()
	{
		URI = null;
		name = null;
		description = null;
		provider = null;
		email = null;
		authoritative = false;
		inputClassURI = null;
		inputClassLabel = "";
		outputClassURI = null;
		outputClassLabel = "";
		parameterClassURI = null;
		parameterClassLabel = "";
		restrictions = new ArrayList<RestrictionBean>();
	}
	
	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getURI()
	 */
	@Override
	public String getURI()
	{
		return URI;
	}

	public void setURI(String URI)
	{
		this.URI = URI;
	}

	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getServiceProvider()
	 */
	@Override
	public String getServiceProvider()
	{
		return provider;
	}
	
	public void setServiceProvider(String provider)
	{
		this.provider = provider;
	}
	
	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getContactEmail()
	 */
	@Override
	public String getContactEmail()
	{
		return email;
	}
	
	public void setContactEmail(String email)
	{
		this.email = email;
	}
	
	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#isAuthoritative()
	 */
	@Override
	public boolean isAuthoritative()
	{
		return authoritative;
	}
	
	public void setAuthoritative(boolean authoritative)
	{
		this.authoritative = authoritative;
	}

	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getInputClassURI()
	 */
	@Override
	public String getInputClassURI()
	{
		return inputClassURI;
	}

	public void setInputClassURI(String inputClassURI)
	{
		this.inputClassURI = inputClassURI;
	}

	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getInputClassLabel()
	 */
	@Override
	public String getInputClassLabel()
	{
		return inputClassLabel;
	}

	public void setInputClassLabel(String inputClassLabel)
	{
		this.inputClassLabel = inputClassLabel;
	}

	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getOutputClassURI()
	 */
	@Override
	public String getOutputClassURI()
	{
		return outputClassURI;
	}

	public void setOutputClassURI(String outputClassURI)
	{
		this.outputClassURI = outputClassURI;
	}

	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getOutputClassLabel()
	 */
	@Override
	public String getOutputClassLabel()
	{
		return outputClassLabel;
	}

	public void setOutputClassLabel(String outputClassLabel)
	{
		this.outputClassLabel = outputClassLabel;
	}

	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getRestrictions()
	 */
	@Override
	public Collection<RestrictionBean> getRestrictions()
	{
		return restrictions;
	}

	public void setRestrictions(Collection<RestrictionBean> restrictions)
	{
		this.restrictions = restrictions;
	}

	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getParameterClassURI()
	 */
	@Override
	public String getParameterClassURI()
	{
		return parameterClassURI;
	}
	
	public void setParameterClassURI(String parameterClassURI)
	{
		this.parameterClassURI = parameterClassURI;
	}

	/* (non-Javadoc)
	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getParameterClassLabel()
	 */
	@Override
	public String getParameterClassLabel()
	{
		return parameterClassLabel;
	}

	public void setParameterClassLabel(String parameterClassLabel)
	{
		this.parameterClassLabel = parameterClassLabel;
	}

//	/* (non-Javadoc)
//	 * @see ca.wilkinsonlab.sadi.ServiceDescription#getParameterDefaultInstanceURI()
//	 */
//	@Override
//	public String getParameterDefaultInstanceURI()
//	{
//		return parameterInstanceURI;
//	}
//	
//	public void setParameterDefaultInstanceURI(String parameterInstanceURI)
//	{
//		this.parameterInstanceURI = parameterInstanceURI;
//	}
}