#-----------------------------------------------------------------
# semanticscience::org::resource::SIO_010083
# Generated: 12-Aug-2010 09:33:25 PDT
# Contact: Edward Kawas <edward.kawas+owl2perl@gmail.com>
#-----------------------------------------------------------------
package semanticscience::org::resource::SIO_010083;

use semanticscience::org::resource::SIO_000243;

no strict;
use vars qw( @ISA );
@ISA = qw( semanticscience::org::resource::SIO_000243 );
use strict;

{
    my %_allowed = (

    );

    sub _accessible {
        my ( $self, $attr ) = @_;
        exists $_allowed{$attr} or $self->SUPER::_accessible($attr);
    }

    sub _attr_prop {
        my ( $self, $attr_name, $prop_name ) = @_;
        my $attr = $_allowed{$attr_name};
        return ref($attr) ? $attr->{$prop_name} : $attr if $attr;
        return $self->SUPER::_attr_prop( $attr_name, $prop_name );
    }
}

#-----------------------------------------------------------------
# init
#-----------------------------------------------------------------
sub init {
    my ($self) = shift;
    $self->SUPER::init();
    
    # set the range of this object property
    $self->range('_:genid4c6413661d43');    
    # set the domain for this object property
    $self->domain('http://semanticscience.org/resource/SIO_010007');    
    # set the uri for this object property
    $self->uri('http://semanticscience.org/resource/SIO_010083');
}

1;
__END__

=head1 NAME

semanticscience::org::resource::SIO_010083 - an object propery

=head1 SYNOPSIS

  use semanticscience::org::resource::SIO_010083;
  my $property = semanticscience::org::resource::SIO_010083->new();

  # get the domain of this property
  my $domain = $property->domain;

  # get the range of this property
  my $range = $property->range;

  # get the uri for this property
  my $uri = $property->uri;

=head1 DESCRIPTION

I<Inherits from>: L<http://semanticscience.org/resource/SIO_000243|semanticscience::org::resource::SIO_000243>

=cut
