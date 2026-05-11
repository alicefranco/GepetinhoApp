package com.example.gepetinho.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.gepetinho.R
import com.example.gepetinho.domain.model.PokemonDetails
import com.example.gepetinho.ui.theme.GepetinhoTheme

@Composable
fun DetailsRoute(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onFavoriteClick = viewModel::toggleFavorite,
        onRetryClick = viewModel::refreshPokemonDetails,
        onDismissError = viewModel::clearError,
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(
    uiState: DetailsUiState,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onRetryClick: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBackClick) {
                    Text(text = "Back")
                }

                Spacer(modifier = Modifier.weight(1f))

                uiState.pokemon?.let { pokemon ->
                    IconButton(onClick = onFavoriteClick) {
                        Image(
                            painter = painterResource(
                                id = if (pokemon.isFavorite) {
                                    R.drawable.heart
                                } else {
                                    R.drawable.heart_outline
                                }
                            ),
                            contentDescription = if (pokemon.isFavorite) {
                                "Remove favorite"
                            } else {
                                "Add favorite"
                            },
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            uiState.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = onDismissError) {
                        Text(text = "Dismiss")
                    }
                }
            }

            when {
                uiState.pokemon == null && uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.pokemon == null -> {
                    EmptyDetailsState(
                        onRetryClick = onRetryClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    val pokemon = uiState.pokemon

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        AsyncImage(
                            model = pokemon.imageUrl,
                            contentDescription = "${pokemon.name} image",
                            modifier = Modifier.size(180.dp),
                            placeholder = painterResource(R.drawable.pokemon_image_placeholder),
                            error = painterResource(R.drawable.pokemon_image_placeholder),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = pokemon.name.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "No. ${pokemon.id}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pokemon.types.forEach { type ->
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(text = type.replaceFirstChar { it.uppercase() })
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        DetailStats(pokemon = pokemon)

                        if (uiState.isLoading) {
                            Spacer(modifier = Modifier.height(20.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailStats(
    pokemon: PokemonDetails,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatRow(label = "Height", value = "${pokemon.height}")
        StatRow(label = "Weight", value = "${pokemon.weight}")
        StatRow(label = "Base XP", value = pokemon.baseExperience?.toString() ?: "Unknown")
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyDetailsState(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "No Pokemon details loaded yet.",
                style = MaterialTheme.typography.bodyLarge
            )
            Button(onClick = onRetryClick) {
                Text(text = "Retry")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailsScreenPreview() {
    GepetinhoTheme {
        DetailsScreen(
            uiState = DetailsUiState(
                pokemon = PokemonDetails(
                    id = 1,
                    name = "bulbasaur",
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
                    height = 7,
                    weight = 69,
                    baseExperience = 64,
                    types = listOf("grass", "poison"),
                    isFavorite = true
                )
            ),
            onBackClick = {},
            onFavoriteClick = {},
            onRetryClick = {},
            onDismissError = {}
        )
    }
}
