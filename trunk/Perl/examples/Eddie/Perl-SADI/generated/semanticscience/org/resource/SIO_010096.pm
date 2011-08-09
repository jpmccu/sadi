#-----------------------------------------------------------------
# semanticscience::org::resource::SIO_010096
# Generated: 12-Aug-2010 09:35:30 PDT
# Contact: Edward Kawas <edward.kawas+owl2perl@gmail.com>
#-----------------------------------------------------------------
package semanticscience::org::resource::SIO_010096;

use OWL::Data::OWL::Class;
use Blank::genid4c641366812d;
use semanticscience::org::resource::SIO_010099;


no strict;
use vars qw( @ISA );
@ISA = qw(
 OWL::Data::OWL::Class
 Blank::genid4c641366812d
 semanticscience::org::resource::SIO_010099 
);
use strict;

# imports 
use OWL::Data::String;
use OWL::Data::Integer;
use OWL::Data::Float;
use OWL::Data::Boolean;
use OWL::Data::DateTime;

use OWL::RDF::Predicates::DC_PROTEGE;
use OWL::RDF::Predicates::OMG_LSID;
use OWL::RDF::Predicates::OWL;
use OWL::RDF::Predicates::RDF;
use OWL::RDF::Predicates::RDFS;


use semanticscience::org::resource::SIO_010082;
use semanticscience::org::resource::SIO_000244;



{
    my %_allowed = (

        SIO_010082 => {
        	type => 'OWL::Data::OWL::Class',
        	# range checking of classes added 
        	post => sub {
        		my ($self) = shift;
        		return unless $self->strict;
                my $type = @{$self->SIO_010082}[-1] if defined $self->SIO_010082;
                return unless defined $type and $type->type;
                my $range = new semanticscience::org::resource::SIO_010082->range();
                return unless $range;
                $range = $self->uri2package($range);
                eval {
                	$range = $range->new();
                };
                return if $@;
        		$self->throw("\n" . $type->type() . "\nis not related to\n" . $range->type()) unless $type->isa(ref($range));
        	},
        	is_array => 1, },

        SIO_000244 => {
        	type => 'OWL::Data::OWL::Class',
        	# range checking of classes added 
        	post => sub {
        		my ($self) = shift;
        		return unless $self->strict;
                my $type = @{$self->SIO_000244}[-1] if defined $self->SIO_000244;
                return unless defined $type and $type->type;
                my $range = new semanticscience::org::resource::SIO_000244->range();
                return unless $range;
                $range = $self->uri2package($range);
                eval {
                	$range = $range->new();
                };
                return if $@;
        		$self->throw("\n" . $type->type() . "\nis not related to\n" . $range->type()) unless $type->isa(ref($range));
        	},
        	is_array => 1, },


    );

    sub _accessible {
        my ( $self, $attr ) = @_;
        exists $_allowed{$attr} 
          or $self->OWL::Data::OWL::Class::_accessible($attr)
          or $self->Blank::genid4c641366812d::_accessible($attr)
          or $self->semanticscience::org::resource::SIO_010099::_accessible($attr)
    }

    sub _attr_prop {
        my ( $self, $attr_name, $prop_name ) = @_;
        my $attr = $_allowed{$attr_name};
        return ref($attr) ? $attr->{$prop_name} : $attr if $attr;
        return $self->OWL::Data::OWL::Class::_attr_prop( $attr_name, $prop_name ) 
            if $self->OWL::Data::OWL::Class::_accessible($attr_name);
        return $self->Blank::genid4c641366812d::_attr_prop( $attr_name, $prop_name ) 
            if $self->Blank::genid4c641366812d::_accessible($attr_name);
        return $self->semanticscience::org::resource::SIO_010099::_attr_prop( $attr_name, $prop_name ) 
            if $self->semanticscience::org::resource::SIO_010099::_accessible($attr_name);

    }
}

#-----------------------------------------------------------------
# init
#-----------------------------------------------------------------
sub init {
    my ($self) = shift;
    $self->OWL::Data::OWL::Class::init();
    $self->Blank::genid4c641366812d::init();
    $self->semanticscience::org::resource::SIO_010099::init();
    $self->type('http://semanticscience.org/resource/SIO_010096');

}

# returns an RDF::Core::Enumerator object or undef;
sub _get_statements {
	# use_type is undef initially and 1 for super() calls
	my ($self) = @_;

    # create a named resource or bnode for this object ....
    my $subject = new RDF::Core::Resource( $self->value ) if defined $self->value and $self->value ne '';
    $subject = 
        new RDF::Core::Resource( "_:a" . sprintf( "%08x%04x", time(), int(rand(0xFFFF))) )
      unless defined $self->value and $self->value ne '';
    # set the subject so that this sub graph can be linked to other graphs
    $self->subject($subject);
    
    my $add_type_statement = defined $self->value and $self->value ne '' ? 1 : undef;

    # in case we have a bnode
    $self->value($subject->getURI) unless defined $self->value and $self->value ne '';

    # add parent statements too
    eval {
    	# add parent statements
        $self->OWL::Data::OWL::Class::_get_statements();
    };
    eval {
    	# add parent statements
        $self->Blank::genid4c641366812d::_get_statements();
    };
    eval {
    	# add parent statements
        $self->semanticscience::org::resource::SIO_010099::_get_statements();
    };

    # for each attribute, if is array do:
    if (defined $self->SIO_010082){
	    foreach (@{$self->SIO_010082}) {
	        # add all statements from $_
	        my $enumerator = $_->_get_statements if defined $_->_get_statements;
	        next unless defined $enumerator;
            my $statement = $enumerator->getFirst;
            while (defined $statement) {
              $self->model->addStmt($statement);
              $statement = $enumerator->getNext
            }
            $enumerator->close;
	        # add a statement linking the graphs
	        $self->model->addStmt(
	          new RDF::Core::Statement(
	              $subject,
	              $subject->new('http://semanticscience.org/resource/SIO_010082'), 
	              (defined $_->subject ? $_->subject : RDF::Core::Resource->new($_->value))
	          )
	        );
	        $add_type_statement = 1;
	    }
    }
    # for each attribute, if is array do:
    if (defined $self->SIO_000244){
	    foreach (@{$self->SIO_000244}) {
	        # add all statements from $_
	        my $enumerator = $_->_get_statements if defined $_->_get_statements;
	        next unless defined $enumerator;
            my $statement = $enumerator->getFirst;
            while (defined $statement) {
              $self->model->addStmt($statement);
              $statement = $enumerator->getNext
            }
            $enumerator->close;
	        # add a statement linking the graphs
	        $self->model->addStmt(
	          new RDF::Core::Statement(
	              $subject,
	              $subject->new('http://semanticscience.org/resource/SIO_000244'), 
	              (defined $_->subject ? $_->subject : RDF::Core::Resource->new($_->value))
	          )
	        );
	        $add_type_statement = 1;
	    }
    }

    # add a label if one is specified
    if (defined $self->label()) {
        $self->model->addStmt(
           new RDF::Core::Statement(
            $subject,
            $subject->new( OWL::RDF::Predicates::RDFS->label ),
            new RDF::Core::Literal( $self->label )
           )
        );
    }
    
    # add the type
    $self->model->addStmt(
        new RDF::Core::Statement(
            $subject,
            $subject->new( OWL::RDF::Predicates::RDF->type ),
            new RDF::Core::Resource( $self->type )
        )
    ) if $add_type_statement;
    return $self->model()->getStmts();
}

1;

__END__

=head1 NAME

semanticscience::org::resource::SIO_010096 - an automatically generated owl class!

=cut

=head1 SYNOPSIS

  use semanticscience::org::resource::SIO_010096;
  my $class = semanticscience::org::resource::SIO_010096->new();

  # get the uri for this class
  my $uri = $class->uri;
  # add object properties 
  # Make sure to use the appropriate OWL class! 
  # OWL::Data::OWL::Class used as an example
  $class->add_SIO_010082(new OWL::Data::OWL::Class('#someURI'));

  # get the SIO_010082 object properties
  my $SIO_010082_objects = $class->SIO_010082;
  # Make sure to use the appropriate OWL class! 
  # OWL::Data::OWL::Class used as an example
  $class->add_SIO_000244(new OWL::Data::OWL::Class('#someURI'));

  # get the SIO_000244 object properties
  my $SIO_000244_objects = $class->SIO_000244;

=cut

=head1 DESCRIPTION

I<Inherits from>:
L<OWL::Data::OWL::Class>
L<Blank::genid4c641366812d>
L<semanticscience::org::resource::SIO_010099>




=cut
