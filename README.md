# Blue Zone API — Guia para o Front-end 

---

## O que é essa API e por que ela existe?

O fluxo é simples:

```
App React Native  →  manda pedido para a API  →  API mexe na planilha  →  responde para o app
```

Vocês não precisam saber como a API funciona por dentro. Só precisam saber **como pedir as coisas para ela** — é exatamente isso que esse guia ensina.

---

## Informações básicas

**Endereço base da API:**
```
https://blue-zone-api.onrender.com
```

Todos os pedidos começam com esse endereço.

> ⚠️ **Aviso importante:** a API "dorme" quando fica muito tempo sem uso. Se ninguém usar por mais de 15 minutos, a primeira chamada pode demorar até **60 segundos** para responder. Depois disso volta ao normal. Não é bug, é limitação do plano gratuito do servidor.

---

## O objeto Item — o que é e quais campos tem

Tudo no inventário é um **Item**. Quando a API te mandar um item, ou quando você mandar um item para a API, ele sempre vai ter essa estrutura:

```json
{
    "categoriaPrincipal": "Kits e Protocolos",
    "subcategoria": "Emagrecimento",
    "item": "Cafeína 100mg/2ml",
    "controlado": "Sim",
    "quantidade": "10",
    "estoqueMinimo": "5",
    "dataVencimento": "31/12/2026",
    "status": "",
    "observacoes": "Qualquer anotação aqui",
    "ultimaAtualizacao": ""
}
```

### O que cada campo significa

| Campo | O que é | Exemplo |
|---|---|---|
| `categoriaPrincipal` | Categoria grande do item | `"Medicamentos"` |
| `subcategoria` | Subdivisão dentro da categoria | `"Primeiros Socorros"` |
| `item` | **Nome do produto — identificador único** | `"Dipirona 1g/2ml"` |
| `controlado` | Se é medicamento controlado | `"Sim"` ou `""` |
| `quantidade` | Quantidade atual em estoque | `"10"` |
| `estoqueMinimo` | Quantidade mínima antes de alertar | `"5"` |
| `dataVencimento` | Data de vencimento | `"31/12/2026"` |
| `status` | Calculado automaticamente | sempre mande `""` |
| `observacoes` | Qualquer anotação livre | `"Lote 2024"` |
| `ultimaAtualizacao` | Calculado automaticamente | sempre mande `""` |

### Regras que vocês precisam seguir

**O campo `item` é o mais importante.** É com ele que a API sabe qual produto editar. Se mandarem o nome errado ou com um espaço a mais, vai dar erro.

**Números são strings aqui.** Quantidade `10` se escreve como `"10"` (com aspas), não como o número `10`. Isso porque a planilha pode ter células vazias, e `""` é mais fácil de tratar do que `null`.

**Data no formato brasileiro.** `"31/12/2026"` — dia/mês/ano. Não `2026-12-31`.

**`controlado` só tem dois valores possíveis:** `"Sim"` ou `""` (string vazia). Nada de `"Não"`, `"não"`, `false`, etc.

**Campos que não foram preenchidos:** mandem `""` (string vazia). Nunca `null` ou `undefined`.

**`status` e `ultimaAtualizacao`:** nunca precisam preencher. Mandem sempre `""`.

---

## Categorias disponíveis

Usem **exatamente** esses textos — acento, maiúscula, tudo igual. A API é sensível a isso.

### Categorias principais e suas subcategorias

**`"Metabólicos e Nutraceuticos"`**
- `"Energia e Metabolismo"`
- `"Antioxidantes e Detox"`
- `"Neurocognitivos / Nootrópicos"`
- `"Performance e Composição Corporal"`
- `"Saúde da Pele"`
- `"Minerais e Cofatores"`

**`"Vitaminas e Micronutrientes"`**
- `"Aminoácidos"`
- `"Vitaminas"`

**`"Kits e Protocolos"`**
- `"Emagrecimento"`
- `"Alopecia"`
- `"Estrias"`

**`"Injetáveis Estéticos"`**
- `"Bioestimuladores de Colágeno"`
- `"Toxina Botulínica"`

**`"Hormônios"`**
- `"Moduladores de Eixo"`

**`"Implantes"`**
- `"Hormonais e Não Hormonais"`

**`"Medicamentos"`**
- `"Primeiros Socorros"`
- `"Anestésicos Locais"`

**`"Insumos"`**
- `"Soluções e Diluentes"`

**`"Materiais Médico-Hospitalares"`**
- `"Agulhas Descartáveis"`
- `"Curativos e Higiene"`
- `"Material de Consumo / Descartáveis"`
- `"Materiais Estéreis"`
- `"Dispositivos de Infusão"`
- `"Cateteres"`

---

## Os 4 pedidos que vocês vão fazer

---

### 1. Buscar todos os itens

**Quando usar:** ao carregar a tela de Estoque.

```javascript
const buscarItens = async () => {
    const response = await fetch('https://blue-zone-api.onrender.com/itens');
    const itens = await response.json();
    return itens; // array com todos os itens
};
```

**O que você recebe:** um array com todos os itens da planilha. Cada elemento do array é um objeto Item com os campos descritos acima.

---

### 2. Buscar alertas de estoque

**Quando usar:** na tela inicial (Home), para mostrar os itens críticos (como ta agr).

```javascript
const buscarAlertas = async () => {
    const response = await fetch('https://blue-zone-api.onrender.com/alertas');
    const alertas = await response.json();
    return alertas; // só os itens que estão em situação crítica
};
```

**O que você recebe:** um array só com os itens que estão com quantidade abaixo do mínimo **ou** com vencimento nos próximos 30 dias. Mesmo formato do `/itens`.

Para mostrar o número de alertas no card da Home:

```javascript
const alertas = await buscarAlertas();
const totalAlertas = alertas.length; // ex: 2
```

---

### 3. Adicionar um item novo

**Quando usar:** quando o usuário preencher o formulário de "Adicionar produto" e apertar Salvar.

```javascript
const adicionarItem = async (dadosDoFormulario) => {
    const novoItem = {
        categoriaPrincipal: dadosDoFormulario.categoria,
        subcategoria: dadosDoFormulario.subcategoria,
        item: dadosDoFormulario.nome,           // nome do produto
        controlado: dadosDoFormulario.controlado ? "Sim" : "",
        quantidade: String(dadosDoFormulario.quantidade),
        estoqueMinimo: String(dadosDoFormulario.estoqueMinimo),
        dataVencimento: dadosDoFormulario.dataVencimento ?? "",
        status: "",
        observacoes: dadosDoFormulario.observacoes ?? "",
        ultimaAtualizacao: ""
    };

    const response = await fetch('https://blue-zone-api.onrender.com/itens', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(novoItem)
    });

    if (response.ok) {
        console.log('Item adicionado com sucesso!');
    } else {
        const erro = await response.text();
        console.error('Erro ao adicionar:', erro);
    }
};
```

---

### 4. Editar um item existente

**Quando usar:** quando o usuário alterar a quantidade ou qualquer campo de um item já cadastrado.

> ⚠️ **Atenção:** para editar, vocês precisam mandar o objeto **completo**, não só o campo que mudou. A API substitui a linha inteira na planilha. Então o fluxo correto é: buscar o item → alterar o campo → mandar tudo de volta.

```javascript
const editarItem = async (itemCompleto) => {
    // itemCompleto é o objeto do item que vocês já têm,
    // com o campo que mudou já atualizado

    const response = await fetch('https://blue-zone-api.onrender.com/itens', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(itemCompleto)
    });

    if (response.ok) {
        console.log('Item atualizado com sucesso!');
    } else {
        const erro = await response.text();
        console.error('Erro ao atualizar:', erro);
        // Erro mais comum: "Item não encontrado: NomeDoItem"
        // Isso significa que o campo "item" não bate com nenhum nome na planilha
    }
};

// Exemplo de uso: atualizar só a quantidade
const atualizarQuantidade = async (item, novaQuantidade) => {
    const itemAtualizado = {
        ...item,                              // copia todos os campos do item
        quantidade: String(novaQuantidade)    // sobrescreve só a quantidade
    };
    await editarItem(itemAtualizado);
};
```

---

## Como saber se um item está em alerta (no front)

O endpoint `/alertas` já faz esse trabalho, mas se precisarem verificar um item individual que já está carregado na memória:

```javascript
const itemEstaEmAlerta = (item) => {
    // Verifica estoque baixo
    const quantidade = parseInt(item.quantidade);
    const minimo = parseInt(item.estoqueMinimo);
    if (!isNaN(quantidade) && !isNaN(minimo) && quantidade < minimo) {
        return true;
    }

    // Verifica vencimento próximo (30 dias)
    if (item.dataVencimento) {
        const [dia, mes, ano] = item.dataVencimento.split('/');
        const vencimento = new Date(ano, mes - 1, dia);
        const hoje = new Date();
        const diffEmDias = (vencimento - hoje) / (1000 * 60 * 60 * 24);
        if (diffEmDias <= 30) return true;
    }

    return false;
};

// Uso:
if (itemEstaEmAlerta(item)) {
    // mostra badge "Crítico"
}
```

---

## Tratando erros corretamente

Sempre verifiquem se a resposta deu certo antes de continuar. A API devolve:

- **Status 200** → deu certo
- **Status 500** → algo deu errado, o corpo da resposta tem a mensagem de erro em texto

```javascript
const response = await fetch('https://blue-zone-api.onrender.com/itens', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(item)
});

if (response.ok) {
    // sucesso — atualiza a tela
} else {
    const mensagemDeErro = await response.text();
    // mostra mensagem para o usuário
    Alert.alert('Erro', mensagemDeErro);
}
```

---

## Exemplo completo — tela de Estoque

```javascript
import { useEffect, useState } from 'react';

const TelaEstoque = () => {
    const [itens, setItens] = useState([]);
    const [carregando, setCarregando] = useState(true);

    useEffect(() => {
        carregarItens();
    }, []);

    const carregarItens = async () => {
        setCarregando(true);
        const response = await fetch('https://blue-zone-api.onrender.com/itens');
        const data = await response.json();
        setItens(data);
        setCarregando(false);
    };

    // ... resto do componente
};
```

---

## Resumo rápido

| O que fazer | Método | Endpoint |
|---|---|---|
| Listar todos os itens | GET | `/itens` |
| Ver alertas críticos | GET | `/alertas` |
| Adicionar item novo | POST | `/itens` |
| Editar item existente | PUT | `/itens` |
